package com.project.techstore.auth.service;

import com.project.techstore.auth.dto.AuthResponse;
import com.project.techstore.auth.dto.LoginRequest;
import com.project.techstore.auth.dto.RefreshTokenRequest;
import com.project.techstore.auth.dto.RegisterRequest;
import com.project.techstore.auth.security.JwtService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.AuthProvider;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.entity.UserStatus;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS, "Email này đã được đăng ký trên hệ thống");
        }

        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .phone(request.getPhone() != null ? request.getPhone().trim() : null)
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Đăng ký thành công tài khoản mới: {}", email);

        return createAuthResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không chính xác"));

        if (user.getPassword() == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên");
        }

        log.info("Người dùng đăng nhập thành công: {}", email);

        return createAuthResponse(user);
    }

    @Transactional
    public User loginWithOAuth2(
            String registrationId,
            String providerSubject,
            String email,
            String fullName,
            boolean emailVerified
    ) {
        if (!StringUtils.hasText(providerSubject) || !StringUtils.hasText(email)) {
            throw new AppException(
                    ErrorCode.INVALID_REQUEST,
                    "OAuth provider không trả về đủ thông tin tài khoản"
            );
        }

        if (!emailVerified) {
            throw new AppException(
                    ErrorCode.ACCESS_DENIED,
                    "Email OAuth chưa được xác thực"
            );
        }

        final AuthProvider provider;
        try {
            provider = AuthProvider.fromRegistrationId(registrationId);
        } catch (IllegalArgumentException ex) {
            throw new AppException(ErrorCode.INVALID_REQUEST, ex.getMessage());
        }

        User linkedUser = userRepository
                .findByAuthProviderAndProviderSubject(provider, providerSubject)
                .orElse(null);

        if (linkedUser != null) {
            ensureUserCanLogin(linkedUser);
            return linkedUser;
        }

        if (userRepository.findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new AppException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "Email này đã tồn tại. Vui lòng đăng nhập tài khoản hiện tại rồi liên kết Google."
            );
        }

        String normalizedEmail = email.trim().toLowerCase();
        int emailSeparator = normalizedEmail.indexOf('@');
        String normalizedFullName = StringUtils.hasText(fullName)
                ? fullName.trim()
                : emailSeparator > 0
                    ? normalizedEmail.substring(0, emailSeparator)
                    : normalizedEmail;

        User user = User.builder()
                .email(normalizedEmail)
                .password(null)
                .fullName(normalizedFullName)
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .authProvider(provider)
                .providerSubject(providerSubject)
                .build();

        User savedUser = userRepository.save(user);
        log.info("Tạo tài khoản OAuth thành công: provider={}, email={}", provider, normalizedEmail);
        return savedUser;
    }

    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        try {
            String tokenType = jwtService.extractTokenType(refreshToken);
            if (!"REFRESH".equalsIgnoreCase(tokenType)) {
                throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, "Token cung cấp không phải là Refresh Token");
            }

            String email = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng của Token"));

            if (!jwtService.isTokenValid(refreshToken, user)) {
                throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh Token không hợp lệ hoặc đã hết hạn");
            }

            // Sinh cặp Token mới (Refresh Token Rotation)
            log.info("Làm mới Token thành công cho người dùng: {}", email);
            return createAuthResponse(user);

        } catch (Exception e) {
            log.warn("Lỗi khi xử lý làm mới Refresh Token: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh Token không hợp lệ: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(User currentUser) {
        return UserResponse.from(currentUser);
    }

    @Transactional(readOnly = true)
    public AuthResponse issueTokensForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng"));

        ensureUserCanLogin(user);
        return createAuthResponse(user);
    }

    private void ensureUserCanLogin(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(
                    ErrorCode.ACCESS_DENIED,
                    "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên"
            );
        }
    }

    private AuthResponse createAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtService.generateAccessToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(UserResponse.from(user))
                .build();
    }
}

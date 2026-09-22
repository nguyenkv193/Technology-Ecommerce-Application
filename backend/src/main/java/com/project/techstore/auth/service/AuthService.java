package com.project.techstore.auth.service;

import com.project.techstore.auth.dto.AuthResponse;
import com.project.techstore.auth.dto.LoginRequest;
import com.project.techstore.auth.dto.RefreshTokenRequest;
import com.project.techstore.auth.dto.RegisterRequest;
import com.project.techstore.auth.security.JwtService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.entity.UserStatus;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        String accessToken = jwtService.generateAccessToken(savedUser);
        String refreshToken = jwtService.generateRefreshToken(savedUser);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(UserResponse.from(savedUser))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không chính xác"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Email hoặc mật khẩu không chính xác");
        }

        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Tài khoản của bạn đã bị khóa, vui lòng liên hệ quản trị viên");
        }

        log.info("Người dùng đăng nhập thành công: {}", email);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(UserResponse.from(user))
                .build();
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
            String newAccessToken = jwtService.generateAccessToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            log.info("Làm mới Token thành công cho người dùng: {}", email);

            return AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(newRefreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration())
                    .user(UserResponse.from(user))
                    .build();

        } catch (Exception e) {
            log.warn("Lỗi khi xử lý làm mới Refresh Token: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN, "Refresh Token không hợp lệ: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(User currentUser) {
        return UserResponse.from(currentUser);
    }
}

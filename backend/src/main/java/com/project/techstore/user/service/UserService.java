package com.project.techstore.user.service;

import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.AdminUpdateUserRequest;
import com.project.techstore.user.dto.ChangePasswordRequest;
import com.project.techstore.user.dto.UserProfileUpdateRequest;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.entity.UserStatus;
import com.project.techstore.user.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long userId) {
        User user = getUserEntity(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UserProfileUpdateRequest request) {
        User user = getUserEntity(userId);
        user.setFullName(request.getFullName().trim());
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().trim());
        }
        if (request.getAvatarUrl() != null) {
            String avatar = request.getAvatarUrl().trim();
            user.setAvatarUrl(avatar.isEmpty() ? null : avatar);
        }

        User updatedUser = userRepository.save(user);
        log.info("Đã cập nhật thông tin hồ sơ cho user id={}", userId);
        return UserResponse.from(updatedUser);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = getUserEntity(userId);

        if (user.getPassword() == null
                || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS, "Mật khẩu hiện tại không chính xác");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Đã đổi mật khẩu thành công cho user id={}", userId);
    }

    @Transactional(readOnly = true)
    public User getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, "Không tìm thấy người dùng với ID: " + userId));
    }

    @Transactional(readOnly = true)
    public PageResponse<UserResponse> getAllUsersForAdmin(
            String keyword,
            Role role,
            UserStatus status,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(0, page),
                Math.min(100, Math.max(1, size)),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String pattern = "%" + keyword.trim().toLowerCase(Locale.ROOT) + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(cb.coalesce(root.get("phone"), "")), pattern)
                ));
            }
            if (role != null) {
                predicates.add(cb.equal(root.get("role"), role));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<User> users = userRepository.findAll(spec, pageable);
        return PageResponse.of(users.map(UserResponse::from));
    }

    @Transactional
    public UserResponse updateUserByAdmin(Long currentAdminId, Long targetUserId, AdminUpdateUserRequest request) {
        if (request == null || (request.getRole() == null && request.getStatus() == null)) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Vui lòng chọn vai trò hoặc trạng thái cần cập nhật");
        }

        User target = getUserEntity(targetUserId);

        if (currentAdminId != null && currentAdminId.equals(targetUserId)) {
            if (request.getStatus() != null && request.getStatus() != UserStatus.ACTIVE) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Không thể tự khóa hoặc vô hiệu hóa tài khoản của chính mình");
            }
            if (request.getRole() != null && request.getRole() != Role.ADMIN) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Không thể tự hạ quyền quản trị viên của chính mình");
            }
        }

        if (request.getRole() != null) {
            target.setRole(request.getRole());
        }
        if (request.getStatus() != null) {
            target.setStatus(request.getStatus());
        }

        User saved = userRepository.save(target);
        log.info("Admin id={} đã cập nhật user id={}: role={}, status={}",
                currentAdminId, targetUserId, saved.getRole(), saved.getStatus());
        return UserResponse.from(saved);
    }
}

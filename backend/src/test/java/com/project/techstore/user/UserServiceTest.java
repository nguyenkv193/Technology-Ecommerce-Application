package com.project.techstore.user;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.user.dto.ChangePasswordRequest;
import com.project.techstore.user.dto.UserProfileUpdateRequest;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.entity.UserStatus;
import com.project.techstore.user.repository.UserRepository;
import com.project.techstore.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .password("encoded_secret")
                .fullName("Nguyen Van A")
                .phone("0901234567")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        sampleUser.setId(10L);
    }

    @Test
    void testGetProfileSuccess() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));

        UserResponse result = userService.getProfile(10L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("user@techstore.vn", result.getEmail());
        assertEquals("Nguyen Van A", result.getFullName());
    }

    @Test
    void testGetProfileNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> userService.getProfile(999L));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testUpdateProfileSuccess() {
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Nguyen Van B")
                .phone("0988888888")
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.updateProfile(10L, request);

        assertNotNull(result);
        assertEquals("Nguyen Van B", result.getFullName());
        assertEquals("0988888888", result.getPhone());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testChangePasswordSuccess() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword123")
                .newPassword("NewPassword456")
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("OldPassword123", "encoded_secret")).thenReturn(true);
        when(passwordEncoder.matches("NewPassword456", "encoded_secret")).thenReturn(false);
        when(passwordEncoder.encode("NewPassword456")).thenReturn("new_encoded_secret");

        userService.changePassword(10L, request);

        assertEquals("new_encoded_secret", sampleUser.getPassword());
        verify(userRepository).save(sampleUser);
    }

    @Test
    void testChangePasswordWrongCurrentPassword() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("WrongPassword")
                .newPassword("NewPassword456")
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("WrongPassword", "encoded_secret")).thenReturn(false);

        AppException ex = assertThrows(AppException.class, () -> userService.changePassword(10L, request));
        assertEquals(ErrorCode.INVALID_CREDENTIALS, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void testChangePasswordSameAsOldPassword() {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("OldPassword123")
                .newPassword("OldPassword123")
                .build();

        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(passwordEncoder.matches("OldPassword123", "encoded_secret")).thenReturn(true);
        when(passwordEncoder.matches("OldPassword123", "encoded_secret")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> userService.changePassword(10L, request));
        assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
        verify(userRepository, never()).save(any());
    }
}

package com.project.techstore.common.exception;

import com.project.techstore.common.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Xử lý tập trung toàn bộ ngoại lệ trong hệ thống (Global Exception Handler).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 1. Xử lý ngoại lệ nghiệp vụ tùy chỉnh (AppException).
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Ngoại lệ nghiệp vụ [{}]: {}", errorCode.name(), ex.getMessage());

        ApiResponse<Void> response = ApiResponse.error(errorCode.name(), ex.getMessage());
        return new ResponseEntity<>(response, errorCode.getHttpStatus());
    }

    /**
     * 2. Xử lý lỗi validate DTO với @Valid (@NotNull, @NotBlank, @Size,...).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        log.warn("Lỗi dữ liệu đầu vào không hợp lệ: {}", errors);

        ApiResponse<Map<String, String>> response = ApiResponse.error(
                ErrorCode.VALIDATION_ERROR.name(),
                "Dữ liệu gửi lên không vượt qua kiểm tra ràng buộc",
                errors
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 3. Xử lý lỗi ConstraintViolationException.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Vi phạm ràng buộc validation: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.VALIDATION_ERROR.name(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * 4. Xử lý lỗi xác thực bảo mật (AuthenticationException - chưa login hoặc sai thông tin).
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Lỗi xác thực người dùng: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.UNAUTHORIZED.name(), ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    /**
     * 5. Xử lý lỗi phân quyền (AccessDeniedException - không đủ quyền Admin/User).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Từ chối quyền truy cập: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.ACCESS_DENIED.name(), "Bạn không có quyền thực hiện thao tác này");
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    /**
     * 6. Xử lý lỗi toàn vẹn cơ sở dữ liệu (DataIntegrityViolationException - unique key, foreign key).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Lỗi toàn vẹn dữ liệu Database: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.CONFLICT.name(), "Dữ liệu bị trùng lặp hoặc vi phạm ràng buộc liên kết");
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    /**
     * 7. Xử lý các lỗi bất ngờ chưa lường trước (UnexpectedException).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
        log.error("Lỗi hệ thống bất ngờ: {}", ex.getMessage(), ex);
        ApiResponse<Void> response = ApiResponse.error(
                ErrorCode.INTERNAL_SERVER_ERROR.name(),
                "Hệ thống gặp sự cố trong quá trình xử lý yêu cầu"
        );
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

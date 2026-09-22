package com.project.techstore.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Chuẩn hóa cấu trúc phản hồi toàn cục của hệ thống (Global API Response).
 * @param <T> Kiểu dữ liệu payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Cấu trúc phản hồi API chuẩn của TechStore")
public class ApiResponse<T> {

    @Schema(description = "Trạng thái thành công của yêu cầu", example = "true")
    private boolean success;

    @Schema(description = "Mã lỗi chuẩn hóa (chỉ xuất hiện khi có lỗi)", example = "PRODUCT_NOT_FOUND")
    private String code;

    @Schema(description = "Thông điệp phản hồi hoặc giải thích lỗi", example = "Thao tác thành công")
    private String message;

    @Schema(description = "Dữ liệu trả về")
    private T data;

    @Schema(description = "Thời gian phản hồi")
    @Builder.Default
    private Instant timestamp = Instant.now();

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, T errorDetails) {
        return ApiResponse.<T>builder()
                .success(false)
                .code(code)
                .message(message)
                .data(errorDetails)
                .build();
    }
}

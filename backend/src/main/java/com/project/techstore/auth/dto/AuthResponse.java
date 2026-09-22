package com.project.techstore.auth.dto;

import com.project.techstore.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Kết quả xác thực (Access Token & Refresh Token)")
public class AuthResponse {

    @Schema(description = "Access Token JWT dùng để gọi API", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String accessToken;

    @Schema(description = "Refresh Token JWT dùng để xin cấp mới Access Token", example = "eyJhbGciOiJIUzUxMiJ9...")
    private String refreshToken;

    @Schema(description = "Loại Token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Thời gian hết hạn của Access Token (ms)", example = "1800000")
    private long expiresIn;

    @Schema(description = "Thông tin cơ bản của người dùng")
    private UserResponse user;
}

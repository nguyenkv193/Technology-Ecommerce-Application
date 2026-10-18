package com.project.techstore.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Mã dùng một lần do OAuth callback cấp cho frontend")
public class OAuth2CodeRequest {

    @NotBlank(message = "OAuth code không được để trống")
    @Schema(description = "Mã OAuth dùng một lần", example = "f7f5e4...")
    private String code;
}

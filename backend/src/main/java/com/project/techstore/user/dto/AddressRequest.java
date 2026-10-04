package com.project.techstore.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Yêu cầu tạo mới hoặc cập nhật địa chỉ giao hàng")
public class AddressRequest {

    @NotBlank(message = "Tên người nhận không được để trống")
    @Schema(description = "Tên người nhận hàng", example = "Nguyễn Văn A")
    private String recipientName;

    @NotBlank(message = "Số điện thoại nhận hàng không được để trống")
    @Pattern(regexp = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$", message = "Số điện thoại không đúng định dạng")
    @Schema(description = "Số điện thoại nhận hàng", example = "0987654321")
    private String phone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    @Schema(description = "Tỉnh / Thành phố", example = "Hà Nội")
    private String province;

    @NotBlank(message = "Quận/Huyện không được để trống")
    @Schema(description = "Quận / Huyện", example = "Cầu Giấy")
    private String district;

    @NotBlank(message = "Phường/Xã không được để trống")
    @Schema(description = "Phường / Xã", example = "Dịch Vọng Hậu")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    @Schema(description = "Địa chỉ chi tiết (số nhà, tên đường)", example = "Số 123 đường Xuân Thủy")
    private String addressDetail;

    @Schema(description = "Đặt làm địa chỉ mặc định hay không", example = "true")
    @Builder.Default
    private Boolean isDefault = false;
}

package com.project.techstore.user.dto;

import com.project.techstore.user.entity.Address;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Thông tin địa chỉ giao hàng")
public class AddressResponse {

    @Schema(description = "ID địa chỉ", example = "1")
    private Long id;

    @Schema(description = "Tên người nhận hàng", example = "Nguyễn Văn A")
    private String recipientName;

    @Schema(description = "Số điện thoại nhận hàng", example = "0987654321")
    private String phone;

    @Schema(description = "Tỉnh / Thành phố", example = "Hà Nội")
    private String province;

    @Schema(description = "Quận / Huyện", example = "Cầu Giấy")
    private String district;

    @Schema(description = "Phường / Xã", example = "Dịch Vọng Hậu")
    private String ward;

    @Schema(description = "Địa chỉ chi tiết", example = "Số 123 đường Xuân Thủy")
    private String addressDetail;

    @Schema(description = "Địa chỉ đầy đủ", example = "Số 123 đường Xuân Thủy, Phường Dịch Vọng Hậu, Quận Cầu Giấy, Hà Nội")
    private String fullAddress;

    @Schema(description = "Địa chỉ mặc định hay không", example = "true")
    private Boolean isDefault;

    @Schema(description = "Thời gian tạo")
    private Instant createdAt;

    public static AddressResponse from(Address address) {
        if (address == null) return null;

        String full = String.format("%s, %s, %s, %s",
                address.getAddressDetail(),
                address.getWard(),
                address.getDistrict(),
                address.getProvince());

        return AddressResponse.builder()
                .id(address.getId())
                .recipientName(address.getRecipientName())
                .phone(address.getPhone())
                .province(address.getProvince())
                .district(address.getDistrict())
                .ward(address.getWard())
                .addressDetail(address.getAddressDetail())
                .fullAddress(full)
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .build();
    }
}

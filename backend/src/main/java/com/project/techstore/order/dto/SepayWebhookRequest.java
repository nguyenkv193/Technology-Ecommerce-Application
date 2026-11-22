package com.project.techstore.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Payload Webhook biến động số dư từ SePay")
public class SepayWebhookRequest {

    @Schema(description = "ID giao dịch trên SePay", example = "92704")
    private String id;

    @Schema(description = "Tên ngân hàng (gateway)", example = "Vietcombank")
    private String gateway;

    @Schema(description = "Thời gian giao dịch phía ngân hàng", example = "2026-11-22 11:00:00")
    private String transactionDate;

    @Schema(description = "Số tài khoản ngân hàng nhận", example = "0000000001")
    private String accountNumber;

    @Schema(description = "Tài khoản ảo (VA) nếu có", example = "SBSEPAY0001")
    private String subAccount;

    @Schema(description = "Mã thanh toán SePay tự nhận diện (nếu có)")
    private String code;

    @Schema(description = "Nội dung chuyển khoản", example = "ORD20261122110000A1B2C3")
    private String content;

    @Schema(description = "Loại giao dịch (in / out)", example = "in")
    private String transferType;

    @Schema(description = "Toàn bộ nội dung tin nhắn/mô tả ngân hàng")
    private String description;

    @Schema(description = "Số tiền giao dịch", example = "19990000")
    private BigDecimal transferAmount;

    @Schema(description = "Mã tham chiếu ngân hàng", example = "MBVCB.3278907687")
    private String referenceCode;

    @Schema(description = "Số dư lũy kế")
    private BigDecimal accumulated;
}

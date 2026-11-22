package com.project.techstore.order.controller;

import com.project.techstore.order.service.SepayWebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments/vietqr")
@RequiredArgsConstructor
@Tag(name = "VietQR Payment Webhook", description = "Webhook nhận biến động số dư tự động từ SePay")
public class PaymentWebhookController {

    private final SepayWebhookService sepayWebhookService;

    @PostMapping(value = "/webhook", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Nhận sự kiện giao dịch tiền vào từ SePay (hỗ trợ cả HMAC-SHA256 và API Key)")
    public ResponseEntity<Map<String, Object>> handleSepayWebhook(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestHeader(value = "X-Sepay-Signature", required = false) String signatureHeader,
            @RequestHeader(value = "X-Sepay-Timestamp", required = false) String timestampHeader,
            @RequestBody String rawBody
    ) {
        Map<String, Object> result = sepayWebhookService.processRawWebhook(
                authorizationHeader,
                signatureHeader,
                timestampHeader,
                rawBody
        );
        return ResponseEntity.ok(result);
    }
}

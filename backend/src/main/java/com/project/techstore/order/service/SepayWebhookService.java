package com.project.techstore.order.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.order.dto.SepayWebhookRequest;
import com.project.techstore.order.entity.Order;
import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentMethod;
import com.project.techstore.order.entity.PaymentStatus;
import com.project.techstore.order.entity.PaymentTransaction;
import com.project.techstore.order.repository.OrderRepository;
import com.project.techstore.order.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class SepayWebhookService {

    private static final Pattern ORDER_CODE_PATTERN =
            Pattern.compile("(ORD[-A-Z0-9]{4,35})", Pattern.CASE_INSENSITIVE);
    private static final String HMAC_SHA256 = "HmacSHA256";

    private final OrderRepository orderRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final ObjectMapper objectMapper;

    @Value("${payment.sepay.webhook-api-key:}")
    private String webhookApiKey;

    @Transactional
    public Map<String, Object> processRawWebhook(
            String authorizationHeader,
            String signatureHeader,
            String timestampHeader,
            String rawBody
    ) {
        verifyWebhookAuthentication(authorizationHeader, signatureHeader, timestampHeader, rawBody);

        if (rawBody == null || rawBody.isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Body SePay webhook trống");
        }

        SepayWebhookRequest request;
        try {
            request = objectMapper.readValue(rawBody, SepayWebhookRequest.class);
        } catch (JsonProcessingException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Payload JSON từ SePay không hợp lệ");
        }

        return processVerifiedWebhook(request, rawBody);
    }

    @Transactional
    public Map<String, Object> processWebhook(String authorizationHeader, SepayWebhookRequest request) {
        verifyWebhookAuthentication(authorizationHeader, null, null, null);
        return processVerifiedWebhook(request, null);
    }

    private Map<String, Object> processVerifiedWebhook(SepayWebhookRequest request, String rawBody) {
        if (request == null || request.getId() == null || request.getId().isBlank()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Thiếu ID giao dịch từ SePay webhook");
        }

        String gatewayTxId = "SEPAY-" + request.getId().trim();
        if (paymentTransactionRepository.existsByGatewayTransactionId(gatewayTxId)) {
            log.info("Bỏ qua SePay webhook trùng lặp: gatewayTxId={}", gatewayTxId);
            return buildResult("DUPLICATED", gatewayTxId, null, "Giao dịch đã được xử lý trước đó");
        }

        String transferType = request.getTransferType() != null
                ? request.getTransferType().trim().toLowerCase(Locale.ROOT)
                : "";
        if (!"in".equals(transferType) && !"credit".equals(transferType)) {
            saveTransaction(null, gatewayTxId, request, rawBody, "IGNORED_TRANSFER_TYPE");
            return buildResult("IGNORED_TRANSFER_TYPE", gatewayTxId, null, "Bỏ qua giao dịch không phải tiền vào");
        }

        String candidateCode = extractOrderCode(request);
        if (candidateCode == null) {
            saveTransaction(null, gatewayTxId, request, rawBody, "IGNORED_NO_ORDER_CODE");
            log.warn("Không tìm thấy mã đơn hàng trong nội dung SePay webhook: content={}", request.getContent());
            return buildResult("IGNORED_NO_ORDER_CODE", gatewayTxId, null, "Không tìm thấy mã đơn hàng trong nội dung chuyển khoản");
        }

        Optional<Order> orderOpt = orderRepository.findWithDetailsByNormalizedOrderCode(candidateCode);
        if (orderOpt.isEmpty()) {
            saveTransaction(null, gatewayTxId, request, rawBody, "IGNORED_ORDER_NOT_FOUND");
            log.warn("Không tìm thấy đơn hàng khớp với mã={} (gatewayTxId={})", candidateCode, gatewayTxId);
            return buildResult("IGNORED_ORDER_NOT_FOUND", gatewayTxId, candidateCode, "Không tìm thấy đơn hàng tương ứng");
        }

        Order order = orderOpt.get();

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            saveTransaction(order, gatewayTxId, request, rawBody, "IGNORED_ALREADY_PAID");
            return buildResult("IGNORED_ALREADY_PAID", gatewayTxId, order.getOrderCode(), "Đơn hàng đã được thanh toán trước đó");
        }

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            saveTransaction(order, gatewayTxId, request, rawBody, "IGNORED_ORDER_CANCELLED");
            log.warn("Nhận thanh toán cho đơn hàng đã hủy: orderCode={}, gatewayTxId={}", order.getOrderCode(), gatewayTxId);
            return buildResult("IGNORED_ORDER_CANCELLED", gatewayTxId, order.getOrderCode(), "Đơn hàng đã ở trạng thái hủy");
        }

        if (order.getPaymentMethod() != PaymentMethod.BANK_TRANSFER && order.getPaymentMethod() != PaymentMethod.VNPAY) {
            saveTransaction(order, gatewayTxId, request, rawBody, "IGNORED_PAYMENT_METHOD");
            return buildResult("IGNORED_PAYMENT_METHOD", gatewayTxId, order.getOrderCode(), "Đơn hàng không sử dụng phương thức chuyển khoản QR");
        }

        BigDecimal receivedAmount = request.getTransferAmount() != null ? request.getTransferAmount() : BigDecimal.ZERO;
        if (receivedAmount.compareTo(order.getFinalAmount()) < 0) {
            saveTransaction(order, gatewayTxId, request, rawBody, "IGNORED_AMOUNT_MISMATCH");
            log.warn("Số tiền chuyển khoản chưa đủ cho đơn {}: nhận={}, cần={}",
                    order.getOrderCode(), receivedAmount, order.getFinalAmount());
            return buildResult("IGNORED_AMOUNT_MISMATCH", gatewayTxId, order.getOrderCode(), "Số tiền chuyển khoản nhỏ hơn giá trị đơn hàng");
        }

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaidAt(Instant.now());
        if (order.getOrderStatus() == OrderStatus.PENDING) {
            order.setOrderStatus(OrderStatus.CONFIRMED);
        }
        orderRepository.save(order);

        saveTransaction(order, gatewayTxId, request, rawBody, "MATCHED");
        log.info("Đối soát tự động SePay thành công cho đơn hàng mã={}, số tiền={}, gatewayTxId={}",
                order.getOrderCode(), receivedAmount, gatewayTxId);

        return buildResult("MATCHED", gatewayTxId, order.getOrderCode(), "Xác nhận thanh toán đơn hàng thành công");
    }

    private void verifyWebhookAuthentication(
            String authorizationHeader,
            String signatureHeader,
            String timestampHeader,
            String rawBody
    ) {
        if (webhookApiKey == null || webhookApiKey.isBlank()) {
            log.error("Chưa cấu hình SEPAY_WEBHOOK_API_KEY trên hệ thống");
            throw new AppException(ErrorCode.UNAUTHORIZED, "Webhook API Key chưa được cấu hình");
        }

        String secret = webhookApiKey.trim();

        // 1. Ưu tiên kiểm tra chữ ký HMAC-SHA256 (X-Sepay-Signature + X-Sepay-Timestamp) nếu có
        if (signatureHeader != null && !signatureHeader.isBlank() && rawBody != null) {
            String normalizedSig = signatureHeader.trim();
            if (normalizedSig.regionMatches(true, 0, "sha256=", 0, 7)) {
                normalizedSig = normalizedSig.substring(7).trim();
            }
            normalizedSig = normalizedSig.toLowerCase(Locale.ROOT);

            String signedPayloadWithTs = (timestampHeader != null && !timestampHeader.isBlank())
                    ? timestampHeader.trim() + "." + rawBody
                    : rawBody;
            String expectedHmacWithTs = computeHmacSha256Hex(secret, signedPayloadWithTs);
            String expectedHmacBodyOnly = computeHmacSha256Hex(secret, rawBody);

            if (constantTimeEquals(expectedHmacWithTs, normalizedSig)
                    || constantTimeEquals(expectedHmacBodyOnly, normalizedSig)) {
                return;
            }
            log.warn("Từ chối SePay webhook do chữ ký HMAC-SHA256 không khớp");
            throw new AppException(ErrorCode.UNAUTHORIZED, "Chữ ký HMAC SePay webhook không hợp lệ");
        }

        // 2. Kiểm tra Authorization: Apikey <secret> hoặc Bearer <secret>
        String providedKey = extractProvidedKey(authorizationHeader);
        if (providedKey != null && constantTimeEquals(secret, providedKey)) {
            return;
        }

        log.warn("Từ chối SePay webhook do API Key hoặc chữ ký không hợp lệ");
        throw new AppException(ErrorCode.UNAUTHORIZED, "Webhook API Key không hợp lệ");
    }

    public static String computeHmacSha256Hex(String secret, String message) {
        try {
            Mac mac = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            mac.init(keySpec);
            byte[] rawHmac = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(rawHmac);
        } catch (Exception e) {
            throw new IllegalStateException("Không thể tính HMAC-SHA256", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return false;
        }
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.UTF_8),
                b.getBytes(StandardCharsets.UTF_8)
        );
    }

    private String extractProvidedKey(String header) {
        if (header == null || header.isBlank()) {
            return null;
        }
        String trimmed = header.trim();
        if (trimmed.regionMatches(true, 0, "Apikey ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        if (trimmed.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return trimmed.substring(7).trim();
        }
        return null;
    }

    public String extractOrderCode(SepayWebhookRequest request) {
        String[] sources = new String[]{request.getContent(), request.getCode(), request.getDescription()};
        for (String source : sources) {
            if (source == null || source.isBlank()) {
                continue;
            }
            Matcher matcher = ORDER_CODE_PATTERN.matcher(source);
            if (matcher.find()) {
                return matcher.group(1).toUpperCase(Locale.ROOT);
            }
        }
        return null;
    }

    private void saveTransaction(Order order, String gatewayTxId, SepayWebhookRequest request, String rawBody, String status) {
        String rawJson = rawBody;
        if (rawJson == null) {
            try {
                rawJson = objectMapper.writeValueAsString(request);
            } catch (JsonProcessingException ignored) {
                // Bỏ qua lỗi tuần tự hóa log thô
            }
        }

        PaymentTransaction tx = PaymentTransaction.builder()
                .order(order)
                .gateway(request.getGateway() != null && !request.getGateway().isBlank() ? request.getGateway() : "SEPAY")
                .gatewayTransactionId(gatewayTxId)
                .transferType(request.getTransferType())
                .transferAmount(request.getTransferAmount() != null ? request.getTransferAmount() : BigDecimal.ZERO)
                .content(request.getContent())
                .accountNumber(request.getSubAccount() != null && !request.getSubAccount().isBlank()
                        ? request.getSubAccount()
                        : request.getAccountNumber())
                .referenceCode(request.getReferenceCode())
                .status(status)
                .rawPayload(rawJson)
                .build();

        paymentTransactionRepository.save(tx);
    }

    private Map<String, Object> buildResult(String status, String transactionId, String orderCode, String message) {
        Map<String, Object> res = new LinkedHashMap<>();
        res.put("success", true);
        res.put("status", status);
        res.put("transactionId", transactionId);
        if (orderCode != null) {
            res.put("orderCode", orderCode);
        }
        res.put("message", message);
        return res;
    }
}

package com.project.techstore.order;

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
import com.project.techstore.order.service.SepayWebhookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SepayWebhookServiceTest {

    private static final String TEST_API_KEY = "sepay_test_d4e45f6c241d4400abc4e9d7c32e78ce";

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentTransactionRepository paymentTransactionRepository;

    private SepayWebhookService sepayWebhookService;

    @BeforeEach
    void setUp() {
        sepayWebhookService = new SepayWebhookService(
                orderRepository,
                paymentTransactionRepository,
                new ObjectMapper()
        );
        ReflectionTestUtils.setField(sepayWebhookService, "webhookApiKey", TEST_API_KEY);
    }

    @Test
    void testInvalidApiKeyThrowsUnauthorized() {
        SepayWebhookRequest request = SepayWebhookRequest.builder()
                .id("10001")
                .transferType("in")
                .content("ORD20261122110000ABC123")
                .transferAmount(new BigDecimal("19990000"))
                .build();

        AppException ex = assertThrows(AppException.class,
                () -> sepayWebhookService.processWebhook("Apikey wrong_key", request));
        assertEquals(ErrorCode.UNAUTHORIZED, ex.getErrorCode());
        verifyNoInteractions(orderRepository, paymentTransactionRepository);
    }

    @Test
    void testDuplicateWebhookIsIgnoredIdempotently() {
        when(paymentTransactionRepository.existsByGatewayTransactionId("SEPAY-10002")).thenReturn(true);

        SepayWebhookRequest request = SepayWebhookRequest.builder()
                .id("10002")
                .transferType("in")
                .content("ORD20261122110000ABC123")
                .transferAmount(new BigDecimal("19990000"))
                .build();

        Map<String, Object> result = sepayWebhookService.processWebhook("Apikey " + TEST_API_KEY, request);

        assertEquals("DUPLICATED", result.get("status"));
        verify(orderRepository, never()).save(any());
        verify(paymentTransactionRepository, never()).save(any());
    }

    @Test
    void testMatchingTransferMarksOrderAsPaidAndConfirmed() {
        Order order = Order.builder()
                .orderCode("ORD20261122110000ABC123")
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .finalAmount(new BigDecimal("19990000"))
                .build();
        order.setId(55L);

        when(paymentTransactionRepository.existsByGatewayTransactionId("SEPAY-10003")).thenReturn(false);
        when(orderRepository.findWithDetailsByNormalizedOrderCode("ORD20261122110000ABC123"))
                .thenReturn(Optional.of(order));

        SepayWebhookRequest request = SepayWebhookRequest.builder()
                .id("10003")
                .gateway("Vietcombank")
                .transferType("in")
                .content("MBVCB.123456.ORD20261122110000ABC123 chuyen khoan")
                .transferAmount(new BigDecimal("19990000"))
                .accountNumber("0000000001")
                .subAccount("SBSEPAY001")
                .referenceCode("REF-10003")
                .build();

        Map<String, Object> result = sepayWebhookService.processWebhook("Apikey " + TEST_API_KEY, request);

        assertEquals("MATCHED", result.get("status"));
        assertEquals("ORD20261122110000ABC123", result.get("orderCode"));
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
        assertNotNull(order.getPaidAt());

        verify(orderRepository).save(order);
        ArgumentCaptor<PaymentTransaction> txCaptor = ArgumentCaptor.forClass(PaymentTransaction.class);
        verify(paymentTransactionRepository).save(txCaptor.capture());
        assertEquals("MATCHED", txCaptor.getValue().getStatus());
        assertEquals("SEPAY-10003", txCaptor.getValue().getGatewayTransactionId());
    }

    @Test
    void testInsufficientAmountDoesNotMarkOrderAsPaid() {
        Order order = Order.builder()
                .orderCode("ORD20261122110000ABC123")
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .finalAmount(new BigDecimal("19990000"))
                .build();

        when(paymentTransactionRepository.existsByGatewayTransactionId("SEPAY-10004")).thenReturn(false);
        when(orderRepository.findWithDetailsByNormalizedOrderCode("ORD20261122110000ABC123"))
                .thenReturn(Optional.of(order));

        SepayWebhookRequest request = SepayWebhookRequest.builder()
                .id("10004")
                .transferType("in")
                .content("ORD20261122110000ABC123")
                .transferAmount(new BigDecimal("5000000"))
                .build();

        Map<String, Object> result = sepayWebhookService.processWebhook("Apikey " + TEST_API_KEY, request);

        assertEquals("IGNORED_AMOUNT_MISMATCH", result.get("status"));
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        verify(orderRepository, never()).save(any());
        verify(paymentTransactionRepository).save(any(PaymentTransaction.class));
    }

    @Test
    void testHmacSignatureVerificationViaRawWebhook() {
        Order order = Order.builder()
                .orderCode("ORD20261122042026E7E719")
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(PaymentMethod.BANK_TRANSFER)
                .paymentStatus(PaymentStatus.PENDING)
                .finalAmount(new BigDecimal("6990000"))
                .build();

        when(paymentTransactionRepository.existsByGatewayTransactionId("SEPAY-52683")).thenReturn(false);
        when(orderRepository.findWithDetailsByNormalizedOrderCode("ORD20261122042026E7E719"))
                .thenReturn(Optional.of(order));

        String rawBody = "{\"gateway\":\"Vietcombank\",\"accountNumber\":\"0000000001\",\"subAccount\":\"SBSEPAYOYAL7GBSWVPX\",\"content\":\"ORD20261122042026E7E719\",\"transferType\":\"in\",\"transferAmount\":6990000,\"id\":52683}";
        String timestamp = "1795321599";
        String sig = "sha256=" + SepayWebhookService.computeHmacSha256Hex(TEST_API_KEY, timestamp + "." + rawBody);

        Map<String, Object> result = sepayWebhookService.processRawWebhook(null, sig, timestamp, rawBody);

        assertEquals("MATCHED", result.get("status"));
        assertEquals(PaymentStatus.PAID, order.getPaymentStatus());
        assertEquals(OrderStatus.CONFIRMED, order.getOrderStatus());
    }
}

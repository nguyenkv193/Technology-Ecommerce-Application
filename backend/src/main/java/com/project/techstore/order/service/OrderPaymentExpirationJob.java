package com.project.techstore.order.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaymentExpirationJob {

    private final OrderService orderService;

    @Scheduled(fixedDelayString = "${payment.vietqr.expiration-check-ms:60000}")
    public void cancelExpiredVietQrOrders() {
        int expiredCount = orderService.expireOverdueQrOrders();
        if (expiredCount > 0) {
            log.info("Đã tự động hủy và hoàn kho cho {} đơn hàng VietQR quá hạn thanh toán", expiredCount);
        }
    }
}

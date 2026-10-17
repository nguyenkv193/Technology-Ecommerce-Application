package com.project.techstore.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.order.controller.OrderController;
import com.project.techstore.order.dto.CreateOrderFromCartRequest;
import com.project.techstore.order.dto.OrderResponse;
import com.project.techstore.order.dto.UpdateOrderStatusRequest;
import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentMethod;
import com.project.techstore.order.entity.PaymentStatus;
import com.project.techstore.order.service.OrderService;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;
    private OrderResponse sampleOrderResponse;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .role(Role.USER)
                .build();
        sampleUser.setId(1L);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(sampleUser, null, sampleUser.getAuthorities())
        );

        sampleOrderResponse = OrderResponse.builder()
                .id(100L)
                .orderCode("ORD-20261017-001")
                .recipientName("Nguyen Van A")
                .phoneNumber("0912345678")
                .shippingAddress("Hanoi, Vietnam")
                .orderStatus(OrderStatus.PENDING.name())
                .paymentStatus(PaymentStatus.PENDING.name())
                .paymentMethod(PaymentMethod.COD.name())
                .totalAmount(BigDecimal.valueOf(79990000))
                .build();
    }

    @Test
    void testCheckoutSuccess() throws Exception {
        CreateOrderFromCartRequest request = CreateOrderFromCartRequest.builder()
                .recipientName("Nguyen Van A")
                .phoneNumber("0912345678")
                .shippingAddress("Hanoi, Vietnam")
                .paymentMethod(PaymentMethod.COD)
                .build();

        when(orderService.createOrderFromCart(eq(1L), any(CreateOrderFromCartRequest.class)))
                .thenReturn(sampleOrderResponse);

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đặt hàng thành công"))
                .andExpect(jsonPath("$.data.orderCode").value("ORD-20261017-001"));

        verify(orderService).createOrderFromCart(eq(1L), any(CreateOrderFromCartRequest.class));
    }

    @Test
    void testCheckoutValidationFailed() throws Exception {
        CreateOrderFromCartRequest invalidRequest = CreateOrderFromCartRequest.builder()
                .recipientName("")
                .phoneNumber("")
                .shippingAddress("")
                .build();

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(orderService);
    }

    @Test
    void testGetMyOrders() throws Exception {
        PageResponse<OrderResponse> pageResponse = PageResponse.<OrderResponse>builder()
                .content(List.of(sampleOrderResponse))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(orderService.getUserOrders(1L, 0, 10)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/orders?page=0&size=10").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].orderCode").value("ORD-20261017-001"))
                .andExpect(jsonPath("$.data.totalElements").value(1));

        verify(orderService).getUserOrders(1L, 0, 10);
    }

    @Test
    void testGetOrderDetails() throws Exception {
        when(orderService.getOrderDetails(1L, 100L, false)).thenReturn(sampleOrderResponse);

        mockMvc.perform(get("/api/v1/orders/100").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(100));

        verify(orderService).getOrderDetails(1L, 100L, false);
    }

    @Test
    void testCancelOrder() throws Exception {
        OrderResponse cancelledOrder = OrderResponse.builder()
                .id(100L)
                .orderCode("ORD-20261017-001")
                .orderStatus(OrderStatus.CANCELLED.name())
                .build();

        when(orderService.cancelOrder(1L, 100L, false)).thenReturn(cancelledOrder);

        mockMvc.perform(put("/api/v1/orders/100/cancel").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã hủy đơn hàng thành công"))
                .andExpect(jsonPath("$.data.orderStatus").value("CANCELLED"));

        verify(orderService).cancelOrder(1L, 100L, false);
    }

    @Test
    void testUpdateStatusSuccess() throws Exception {
        UpdateOrderStatusRequest request = UpdateOrderStatusRequest.builder()
                .orderStatus(OrderStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PAID)
                .build();

        OrderResponse confirmedOrder = OrderResponse.builder()
                .id(100L)
                .orderStatus(OrderStatus.CONFIRMED.name())
                .paymentStatus(PaymentStatus.PAID.name())
                .build();

        when(orderService.updateOrderStatus(eq(100L), any(UpdateOrderStatusRequest.class)))
                .thenReturn(confirmedOrder);

        mockMvc.perform(put("/api/v1/orders/100/status")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cập nhật trạng thái đơn hàng thành công"))
                .andExpect(jsonPath("$.data.orderStatus").value("CONFIRMED"));

        verify(orderService).updateOrderStatus(eq(100L), any(UpdateOrderStatusRequest.class));
    }
}

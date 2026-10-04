package com.project.techstore.order;

import com.project.techstore.cart.entity.Cart;
import com.project.techstore.cart.entity.CartItem;
import com.project.techstore.cart.repository.CartRepository;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.order.dto.CreateOrderFromCartRequest;
import com.project.techstore.order.dto.OrderResponse;
import com.project.techstore.order.entity.Order;
import com.project.techstore.order.entity.OrderItem;
import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentMethod;
import com.project.techstore.order.repository.OrderRepository;
import com.project.techstore.order.service.OrderService;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductVariant;
import com.project.techstore.product.repository.ProductVariantRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User sampleUser;
    private Cart sampleCart;
    private ProductVariant sampleVariant;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().email("buyer@example.com").build();
        sampleUser.setId(1L);

        Product sampleProduct = Product.builder().name("MacBook Pro").slug("macbook-pro").build();
        sampleProduct.setId(10L);

        sampleVariant = ProductVariant.builder()
                .product(sampleProduct)
                .sku("MBP-M3")
                .name("16GB RAM")
                .price(new BigDecimal("35000000"))
                .stock(10)
                .build();
        sampleVariant.setId(20L);

        sampleCart = Cart.builder()
                .user(sampleUser)
                .items(new ArrayList<>())
                .build();
        sampleCart.setId(100L);

        CartItem item = CartItem.builder()
                .cart(sampleCart)
                .variant(sampleVariant)
                .quantity(2)
                .build();
        item.setId(500L);
        sampleCart.addItem(item);
    }

    @Test
    void testCheckoutSuccess() {
        CreateOrderFromCartRequest request = CreateOrderFromCartRequest.builder()
                .recipientName("Nguyễn Văn A")
                .phoneNumber("0987654321")
                .shippingAddress("Hà Nội")
                .paymentMethod(PaymentMethod.COD)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findWithDetailsByUserId(1L)).thenReturn(Optional.of(sampleCart));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(1001L);
            return o;
        });

        OrderResponse response = orderService.createOrderFromCart(1L, request);

        assertNotNull(response);
        assertEquals(1001L, response.getId());
        assertEquals("Nguyễn Văn A", response.getRecipientName());
        assertEquals(new BigDecimal("70000000"), response.getFinalAmount());
        assertEquals("PENDING", response.getOrderStatus());
        assertEquals(1, response.getItems().size());

        // Kiểm tra tồn kho đã bị trừ (10 - 2 = 8)
        assertEquals(8, sampleVariant.getStock());
        verify(productVariantRepository, times(1)).save(sampleVariant);

        // Giỏ hàng phải được xóa trắng sau khi checkout
        assertTrue(sampleCart.getItems().isEmpty());
    }

    @Test
    void testCheckoutEmptyCartThrowsException() {
        sampleCart.getItems().clear();

        CreateOrderFromCartRequest request = CreateOrderFromCartRequest.builder()
                .recipientName("Nguyễn Văn A")
                .phoneNumber("0987654321")
                .shippingAddress("Hà Nội")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(cartRepository.findWithDetailsByUserId(1L)).thenReturn(Optional.of(sampleCart));

        AppException ex = assertThrows(AppException.class, () -> orderService.createOrderFromCart(1L, request));
        assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    }

    @Test
    void testCancelOrderRestoresInventory() {
        sampleVariant.setStock(8);

        OrderItem orderItem = OrderItem.builder()
                .variant(sampleVariant)
                .quantity(2)
                .price(new BigDecimal("35000000"))
                .totalPrice(new BigDecimal("70000000"))
                .build();

        Order order = Order.builder()
                .user(sampleUser)
                .orderCode("ORD-123")
                .orderStatus(OrderStatus.PENDING)
                .items(new ArrayList<>(List.of(orderItem)))
                .build();
        order.setId(1001L);
        orderItem.setOrder(order);

        when(orderRepository.findWithDetailsById(1001L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse res = orderService.cancelOrder(1L, 1001L, false);

        assertNotNull(res);
        assertEquals("CANCELLED", res.getOrderStatus());
        // Tồn kho phải được hoàn lại (8 + 2 = 10)
        assertEquals(10, sampleVariant.getStock());
        verify(productVariantRepository, times(1)).save(sampleVariant);
    }
}

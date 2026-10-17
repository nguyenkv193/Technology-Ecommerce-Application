package com.project.techstore.cart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.cart.controller.CartController;
import com.project.techstore.cart.dto.AddToCartRequest;
import com.project.techstore.cart.dto.CartItemResponse;
import com.project.techstore.cart.dto.CartResponse;
import com.project.techstore.cart.dto.UpdateCartItemRequest;
import com.project.techstore.cart.service.CartService;
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

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CartService cartService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;
    private CartResponse sampleCartResponse;

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

        CartItemResponse item = CartItemResponse.builder()
                .id(10L)
                .variantId(100L)
                .productName("MacBook Pro 16")
                .sku("MBP16-M3")
                .price(BigDecimal.valueOf(79990000))
                .quantity(1)
                .subTotal(BigDecimal.valueOf(79990000))
                .build();

        sampleCartResponse = CartResponse.builder()
                .id(1L)
                .items(List.of(item))
                .totalItems(1)
                .totalPrice(BigDecimal.valueOf(79990000))
                .build();
    }

    @Test
    void testGetCart() throws Exception {
        when(cartService.getCart(1L)).thenReturn(sampleCartResponse);

        mockMvc.perform(get("/api/v1/cart").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.totalItems").value(1))
                .andExpect(jsonPath("$.data.items[0].productName").value("MacBook Pro 16"));

        verify(cartService).getCart(1L);
    }

    @Test
    void testAddToCartSuccess() throws Exception {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(100L)
                .quantity(2)
                .build();

        when(cartService.addToCart(eq(1L), any(AddToCartRequest.class))).thenReturn(sampleCartResponse);

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Thêm vào giỏ hàng thành công"))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(cartService).addToCart(eq(1L), any(AddToCartRequest.class));
    }

    @Test
    void testAddToCartValidationFailed() throws Exception {
        AddToCartRequest invalidRequest = AddToCartRequest.builder()
                .variantId(null)
                .quantity(0)
                .build();

        mockMvc.perform(post("/api/v1/cart/items")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(cartService);
    }

    @Test
    void testUpdateItemQuantitySuccess() throws Exception {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(3)
                .build();

        when(cartService.updateQuantity(1L, 10L, 3)).thenReturn(sampleCartResponse);

        mockMvc.perform(put("/api/v1/cart/items/10")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cập nhật số lượng thành công"));

        verify(cartService).updateQuantity(1L, 10L, 3);
    }

    @Test
    void testUpdateItemQuantityValidationFailed() throws Exception {
        UpdateCartItemRequest request = UpdateCartItemRequest.builder()
                .quantity(0)
                .build();

        mockMvc.perform(put("/api/v1/cart/items/10")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(cartService);
    }

    @Test
    void testRemoveItemSuccess() throws Exception {
        when(cartService.removeItem(1L, 10L)).thenReturn(sampleCartResponse);

        mockMvc.perform(delete("/api/v1/cart/items/10").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã xóa sản phẩm khỏi giỏ hàng"));

        verify(cartService).removeItem(1L, 10L);
    }

    @Test
    void testClearCartSuccess() throws Exception {
        when(cartService.clearCart(1L)).thenReturn(sampleCartResponse);

        mockMvc.perform(delete("/api/v1/cart").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã làm trống giỏ hàng"));

        verify(cartService).clearCart(1L);
    }
}

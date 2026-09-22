package com.project.techstore.cart;

import com.project.techstore.cart.dto.AddToCartRequest;
import com.project.techstore.cart.dto.CartResponse;
import com.project.techstore.cart.entity.Cart;
import com.project.techstore.cart.entity.CartItem;
import com.project.techstore.cart.repository.CartItemRepository;
import com.project.techstore.cart.repository.CartRepository;
import com.project.techstore.cart.service.CartService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User sampleUser;
    private Cart sampleCart;
    private ProductVariant sampleVariant;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder().email("cartuser@example.com").build();
        sampleUser.setId(1L);

        sampleCart = Cart.builder()
                .user(sampleUser)
                .items(new ArrayList<>())
                .build();
        sampleCart.setId(10L);

        Product sampleProduct = Product.builder().name("iPhone 16").slug("iphone-16").build();
        sampleProduct.setId(5L);

        sampleVariant = ProductVariant.builder()
                .product(sampleProduct)
                .sku("IP16-128")
                .name("128GB Black")
                .price(new BigDecimal("22990000"))
                .stock(10)
                .status("ACTIVE")
                .build();
        sampleVariant.setId(20L);
    }

    @Test
    void testAddToCartSuccess() {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(20L)
                .quantity(2)
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(sampleCart));
        when(productVariantRepository.findById(20L)).thenReturn(Optional.of(sampleVariant));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse response = cartService.addToCart(1L, request);

        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(2, response.getTotalItems());
        assertEquals(new BigDecimal("45980000"), response.getTotalPrice());
    }

    @Test
    void testAddToCartOutOfStockThrowsException() {
        AddToCartRequest request = AddToCartRequest.builder()
                .variantId(20L)
                .quantity(15) // Stock is only 10
                .build();

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(sampleCart));
        when(productVariantRepository.findById(20L)).thenReturn(Optional.of(sampleVariant));

        AppException ex = assertThrows(AppException.class, () -> cartService.addToCart(1L, request));
        assertEquals(ErrorCode.OUT_OF_STOCK, ex.getErrorCode());
    }

    @Test
    void testRemoveCartItem() {
        CartItem item = CartItem.builder()
                .cart(sampleCart)
                .variant(sampleVariant)
                .quantity(1)
                .build();
        item.setId(99L);
        sampleCart.addItem(item);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(sampleCart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(inv -> inv.getArgument(0));

        CartResponse res = cartService.removeItem(1L, 99L);

        assertNotNull(res);
        assertEquals(0, res.getItems().size());
    }
}

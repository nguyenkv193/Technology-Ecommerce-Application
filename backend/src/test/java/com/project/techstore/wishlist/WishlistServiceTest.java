package com.project.techstore.wishlist;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductVariant;
import com.project.techstore.product.service.ProductService;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import com.project.techstore.wishlist.dto.WishlistResponse;
import com.project.techstore.wishlist.entity.Wishlist;
import com.project.techstore.wishlist.repository.WishlistRepository;
import com.project.techstore.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private WishlistRepository wishlistRepository;

    @Mock
    private ProductService productService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private User sampleUser;
    private Product sampleProduct;
    private Wishlist sampleWishlist;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .build();
        sampleUser.setId(1L);

        ProductVariant variant = ProductVariant.builder()
                .price(BigDecimal.valueOf(79990000))
                .build();

        sampleProduct = Product.builder()
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .variants(List.of(variant))
                .build();
        sampleProduct.setId(100L);

        sampleWishlist = Wishlist.builder()
                .user(sampleUser)
                .product(sampleProduct)
                .build();
        sampleWishlist.setId(50L);
    }

    @Test
    void testGetUserWishlist() {
        when(wishlistRepository.findWithDetailsByUserIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(sampleWishlist));

        List<WishlistResponse> result = wishlistService.getUserWishlist(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("MacBook Pro 16", result.get(0).getProductName());
    }

    @Test
    void testIsWishlisted() {
        when(wishlistRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(true);
        when(wishlistRepository.existsByUserIdAndProductId(1L, 999L)).thenReturn(false);

        assertTrue(wishlistService.isWishlisted(1L, 100L));
        assertFalse(wishlistService.isWishlisted(1L, 999L));
    }

    @Test
    void testAddToWishlistWhenAlreadyExists() {
        when(wishlistRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(true);
        when(wishlistRepository.findByUserIdAndProductId(1L, 100L)).thenReturn(Optional.of(sampleWishlist));

        WishlistResponse result = wishlistService.addToWishlist(1L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getProductId());
        verify(wishlistRepository, never()).save(any());
    }

    @Test
    void testAddToWishlistNewSuccess() {
        when(wishlistRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(productService.getProductEntity(100L)).thenReturn(sampleProduct);
        when(wishlistRepository.save(any(Wishlist.class))).thenAnswer(invocation -> {
            Wishlist saved = invocation.getArgument(0);
            saved.setId(51L);
            return saved;
        });

        WishlistResponse result = wishlistService.addToWishlist(1L, 100L);

        assertNotNull(result);
        assertEquals(100L, result.getProductId());
        verify(wishlistRepository).save(any(Wishlist.class));
    }

    @Test
    void testAddToWishlistUserNotFound() {
        when(wishlistRepository.existsByUserIdAndProductId(1L, 100L)).thenReturn(false);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> wishlistService.addToWishlist(1L, 100L));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testRemoveFromWishlist() {
        doNothing().when(wishlistRepository).deleteByUserIdAndProductId(1L, 100L);

        wishlistService.removeFromWishlist(1L, 100L);

        verify(wishlistRepository).deleteByUserIdAndProductId(1L, 100L);
    }
}

package com.project.techstore.wishlist.service;

import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.service.ProductService;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import com.project.techstore.wishlist.dto.WishlistResponse;
import com.project.techstore.wishlist.entity.Wishlist;
import com.project.techstore.wishlist.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductService productService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<WishlistResponse> getUserWishlist(Long userId) {
        return wishlistRepository.findWithDetailsByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(WishlistResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean isWishlisted(Long userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Transactional
    public WishlistResponse addToWishlist(Long userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            Wishlist existing = wishlistRepository.findByUserIdAndProductId(userId, productId).orElseThrow();
            return WishlistResponse.from(existing);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Product product = productService.getProductEntity(productId);

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        log.info("Người dùng id={} đã thêm sản phẩm id={} vào danh sách yêu thích", userId, productId);
        return WishlistResponse.from(saved);
    }

    @Transactional
    public void removeFromWishlist(Long userId, Long productId) {
        wishlistRepository.deleteByUserIdAndProductId(userId, productId);
        log.info("Người dùng id={} đã xóa sản phẩm id={} khỏi danh sách yêu thích", userId, productId);
    }
}

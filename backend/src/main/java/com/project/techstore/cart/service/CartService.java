package com.project.techstore.cart.service;

import com.project.techstore.cart.dto.AddToCartRequest;
import com.project.techstore.cart.dto.CartResponse;
import com.project.techstore.cart.entity.Cart;
import com.project.techstore.cart.entity.CartItem;
import com.project.techstore.cart.repository.CartItemRepository;
import com.project.techstore.cart.repository.CartRepository;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.entity.ProductVariant;
import com.project.techstore.product.repository.ProductVariantRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart getOrCreateCartEntity(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                    Cart newCart = Cart.builder()
                            .user(user)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    @Transactional
    public CartResponse getCart(Long userId) {
        Cart cart = cartRepository.findWithDetailsByUserId(userId)
                .orElseGet(() -> getOrCreateCartEntity(userId));
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCartEntity(userId);

        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy phiên bản sản phẩm với ID: " + request.getVariantId()));

        if (variant.getStock() < request.getQuantity()) {
            throw new AppException(ErrorCode.OUT_OF_STOCK, "Số lượng trong kho không đủ (" + variant.getStock() + " sản phẩm còn lại)");
        }

        Optional<CartItem> existingItemOpt = cart.getItems().stream()
                .filter(item -> item.getVariant().getId().equals(variant.getId()))
                .findFirst();

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (newQuantity > variant.getStock()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK, "Vượt quá số lượng hàng tồn kho (" + variant.getStock() + " sản phẩm)");
            }
            existingItem.setQuantity(newQuantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .variant(variant)
                    .quantity(request.getQuantity())
                    .build();
            cart.addItem(newItem);
        }

        Cart saved = cartRepository.save(cart);
        log.info("Người dùng id={} đã thêm biến thể SKU={} vào giỏ (SL: {})", userId, variant.getSku(), request.getQuantity());
        return CartResponse.from(saved);
    }

    @Transactional
    public CartResponse updateQuantity(Long userId, Long cartItemId, int quantity) {
        Cart cart = getOrCreateCartEntity(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy món hàng trong giỏ"));

        if (quantity <= 0) {
            cart.removeItem(item);
        } else {
            if (quantity > item.getVariant().getStock()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK, "Số lượng yêu cầu vượt quá tồn kho (" + item.getVariant().getStock() + " sản phẩm)");
            }
            item.setQuantity(quantity);
        }

        Cart saved = cartRepository.save(cart);
        return CartResponse.from(saved);
    }

    @Transactional
    public CartResponse removeItem(Long userId, Long cartItemId) {
        Cart cart = getOrCreateCartEntity(userId);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND, "Không tìm thấy món hàng trong giỏ"));

        cart.removeItem(item);
        Cart saved = cartRepository.save(cart);
        log.info("Người dùng id={} đã xóa món hàng id={} khỏi giỏ", userId, cartItemId);
        return CartResponse.from(saved);
    }

    @Transactional
    public CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCartEntity(userId);
        cart.clearItems();
        Cart saved = cartRepository.save(cart);
        log.info("Đã làm trống giỏ hàng cho người dùng id={}", userId);
        return CartResponse.from(saved);
    }
}

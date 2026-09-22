package com.project.techstore.order.service;

import com.project.techstore.cart.entity.Cart;
import com.project.techstore.cart.entity.CartItem;
import com.project.techstore.cart.repository.CartRepository;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.order.dto.CreateOrderFromCartRequest;
import com.project.techstore.order.dto.OrderResponse;
import com.project.techstore.order.dto.UpdateOrderStatusRequest;
import com.project.techstore.order.entity.Order;
import com.project.techstore.order.entity.OrderItem;
import com.project.techstore.order.entity.OrderStatus;
import com.project.techstore.order.entity.PaymentStatus;
import com.project.techstore.order.repository.OrderRepository;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductVariant;
import com.project.techstore.product.repository.ProductVariantRepository;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse createOrderFromCart(Long userId, CreateOrderFromCartRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Cart cart = cartRepository.findWithDetailsByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND, "Không tìm thấy giỏ hàng"));

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_REQUEST, "Giỏ hàng của bạn đang trống, không thể đặt hàng");
        }

        // 1. Kiểm tra tồn kho và khấu trừ số lượng kho nguyên tử (Inventory Reservation)
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant.getStock() < item.getQuantity()) {
                throw new AppException(ErrorCode.OUT_OF_STOCK,
                        "Sản phẩm " + variant.getName() + " (SKU: " + variant.getSku() + ") chỉ còn " + variant.getStock() + " trong kho");
            }
            // Khấu trừ tồn kho
            variant.setStock(variant.getStock() - item.getQuantity());
            productVariantRepository.save(variant);

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        // 2. Tạo mã đơn hàng duy nhất
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String shortId = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        String orderCode = "ORD-" + timestamp + "-" + shortId;

        // 3. Khởi tạo thực thể Order
        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .recipientName(request.getRecipientName().trim())
                .phoneNumber(request.getPhoneNumber().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .note(request.getNote())
                .totalAmount(totalAmount)
                .shippingFee(BigDecimal.ZERO)
                .discountAmount(BigDecimal.ZERO)
                .finalAmount(totalAmount)
                .orderStatus(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.PENDING)
                .items(new ArrayList<>())
                .build();

        // 4. Lưu dữ liệu Snapshot vào OrderItems
        for (CartItem item : cart.getItems()) {
            ProductVariant variant = item.getVariant();
            Product product = variant.getProduct();

            String thumb = null;
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                thumb = product.getImages().stream()
                        .filter(ProductImage::getIsThumbnail)
                        .map(ProductImage::getUrl)
                        .findFirst()
                        .orElse(product.getImages().get(0).getUrl());
            }

            BigDecimal lineTotal = variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            OrderItem orderItem = OrderItem.builder()
                    .variant(variant)
                    .productName(product.getName())
                    .variantName(variant.getName())
                    .sku(variant.getSku())
                    .thumbnailUrl(thumb)
                    .price(variant.getPrice())
                    .quantity(item.getQuantity())
                    .totalPrice(lineTotal)
                    .build();

            order.addItem(orderItem);
        }

        Order savedOrder = orderRepository.save(order);

        // 5. Làm trống giỏ hàng sau khi đặt thành công
        cart.clearItems();
        cartRepository.save(cart);

        log.info("Người dùng id={} đã đặt đơn hàng thành công: mã={} (tổng tiền={})", userId, orderCode, totalAmount);
        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getUserOrders(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size));
        Page<Order> orderPage = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return PageResponse.of(orderPage.map(OrderResponse::from));
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng với ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền truy cập đơn hàng này");
        }

        return OrderResponse.from(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId, boolean isAdmin) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng với ID: " + orderId));

        if (!isAdmin && !order.getUser().getId().equals(userId)) {
            throw new AppException(ErrorCode.ACCESS_DENIED, "Bạn không có quyền hủy đơn hàng này");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPING ||
            order.getOrderStatus() == OrderStatus.DELIVERED ||
            order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new AppException(ErrorCode.INVALID_REQUEST,
                    "Không thể hủy đơn hàng đang ở trạng thái: " + order.getOrderStatus());
        }

        // Hoàn lại số lượng tồn kho cho từng biến thể
        for (OrderItem item : order.getItems()) {
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                variant.setStock(variant.getStock() + item.getQuantity());
                productVariantRepository.save(variant);
            }
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        Order saved = orderRepository.save(order);
        log.info("Đơn hàng mã={} đã bị hủy và hoàn lại tồn kho", order.getOrderCode());
        return OrderResponse.from(saved);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, UpdateOrderStatusRequest request) {
        Order order = orderRepository.findWithDetailsById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Không tìm thấy đơn hàng với ID: " + orderId));

        OrderStatus oldStatus = order.getOrderStatus();
        OrderStatus newStatus = request.getOrderStatus();

        // Nếu admin chuyển trạng thái sang CANCELLED mà trước đó chưa hủy -> hoàn tồn kho
        if (newStatus == OrderStatus.CANCELLED && oldStatus != OrderStatus.CANCELLED) {
            for (OrderItem item : order.getItems()) {
                ProductVariant variant = item.getVariant();
                if (variant != null) {
                    variant.setStock(variant.getStock() + item.getQuantity());
                    productVariantRepository.save(variant);
                }
            }
        }

        order.setOrderStatus(newStatus);
        if (request.getPaymentStatus() != null) {
            order.setPaymentStatus(request.getPaymentStatus());
        }

        Order saved = orderRepository.save(order);
        log.info("Admin đã cập nhật trạng thái đơn hàng mã={} từ {} sang {}", order.getOrderCode(), oldStatus, newStatus);
        return OrderResponse.from(saved);
    }
}

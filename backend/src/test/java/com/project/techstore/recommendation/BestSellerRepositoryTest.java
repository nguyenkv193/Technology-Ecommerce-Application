package com.project.techstore.recommendation;

import com.project.techstore.brand.entity.Brand;
import com.project.techstore.category.entity.Category;
import com.project.techstore.common.config.JpaAuditingConfig;
import com.project.techstore.order.entity.*;
import com.project.techstore.order.repository.OrderItemRepository;
import com.project.techstore.product.entity.*;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@Import(JpaAuditingConfig.class)
class BestSellerRepositoryTest {
    @Autowired TestEntityManager em;
    @Autowired OrderItemRepository orderItems;
    @Autowired ProductRepository products;

    private Product product(String name, Category category, Brand brand, int stock) {
        var p = Product.builder().name(name).slug(name).category(category).brand(brand).build();
        p.addVariant(ProductVariant.builder().sku(name).name(name).price(BigDecimal.TEN).stock(stock).build());
        return em.persistAndFlush(p);
    }

    private void sale(User user, ProductVariant variant, int quantity, OrderStatus status, PaymentStatus payment) {
        var order = Order.builder().orderCode(UUID.randomUUID().toString()).user(user).recipientName("Buyer")
                .phoneNumber("0123456789").shippingAddress("Address").totalAmount(BigDecimal.TEN)
                .finalAmount(BigDecimal.TEN).orderStatus(status).paymentStatus(payment).build();
        order.addItem(OrderItem.builder().variant(variant).productName("Product").variantName("Variant")
                .sku(variant.getSku()).price(BigDecimal.TEN).quantity(quantity).totalPrice(BigDecimal.TEN).build());
        em.persistAndFlush(order);
    }

    @Test
    void sumsAcrossVariantsAndFiltersOrderStatusPaymentStockCategoryAndExclusions() {
        var category = em.persist(Category.builder().name("Laptop").slug("laptop").build());
        var otherCategory = em.persist(Category.builder().name("Phone").slug("phone").build());
        var brand = em.persist(Brand.builder().name("Brand").slug("brand").build());
        var user = em.persist(User.builder().email("buyer@test.com").password("hash").fullName("Buyer").build());
        var winner = product("winner", category, brand, 1);
        var secondVariant = ProductVariant.builder().product(winner).sku("second-variant").name("Second")
                .price(BigDecimal.TEN).stock(1).build();
        winner.addVariant(secondVariant);
        em.persistAndFlush(secondVariant);
        var runnerUp = product("runner", category, brand, 1);
        var unsold = product("unsold", category, brand, 1);
        var outOfStock = product("out-of-stock", category, brand, 0);
        var inactive = product("inactive", category, brand, 1);
        inactive.setStatus(ProductStatus.INACTIVE);
        var other = product("other", otherCategory, brand, 1);
        sale(user, winner.getVariants().getFirst(), 2, OrderStatus.DELIVERED, PaymentStatus.PAID);
        sale(user, secondVariant, 3, OrderStatus.DELIVERED, PaymentStatus.PAID);
        sale(user, runnerUp.getVariants().getFirst(), 4, OrderStatus.DELIVERED, PaymentStatus.PAID);
        sale(user, unsold.getVariants().getFirst(), 100, OrderStatus.CANCELLED, PaymentStatus.PAID);
        sale(user, unsold.getVariants().getFirst(), 100, OrderStatus.DELIVERED, PaymentStatus.REFUNDED);
        sale(user, unsold.getVariants().getFirst(), 100, OrderStatus.PENDING, PaymentStatus.PAID);
        sale(user, unsold.getVariants().getFirst(), 100, OrderStatus.DELIVERED, PaymentStatus.PENDING);
        sale(user, outOfStock.getVariants().getFirst(), 100, OrderStatus.DELIVERED, PaymentStatus.PAID);
        sale(user, inactive.getVariants().getFirst(), 100, OrderStatus.DELIVERED, PaymentStatus.PAID);
        sale(user, other.getVariants().getFirst(), 1, OrderStatus.DELIVERED, PaymentStatus.PAID);
        em.flush();
        assertEquals(List.of(winner.getId(), runnerUp.getId(), other.getId()),
                orderItems.findBestSellingProductIds(List.of(-1L), null, PageRequest.of(0, 10)));
        assertEquals(List.of(runnerUp.getId()), orderItems.findBestSellingProductIds(
                List.of(winner.getId()), category.getId(), PageRequest.of(0, 10)));
        assertEquals(List.of(winner.getId()), orderItems.findBestSellingProductIds(List.of(-1L), null, PageRequest.of(0, 1)));
        var available = products.findAvailableForRecommendations(List.of(winner.getId()), category.getId(), PageRequest.of(0, 10));
        assertEquals(Set.of(runnerUp.getId(), unsold.getId()), new HashSet<>(available.stream().map(Product::getId).toList()));
    }
}

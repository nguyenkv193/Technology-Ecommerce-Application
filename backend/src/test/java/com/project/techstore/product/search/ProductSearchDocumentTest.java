package com.project.techstore.product.search;

import com.project.techstore.brand.entity.Brand;
import com.project.techstore.category.entity.Category;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductAttribute;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductStatus;
import com.project.techstore.product.entity.ProductVariant;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductSearchDocumentTest {

    @Test
    void mapsCatalogFieldsAndVariantsToSearchDocumentAndApiResponse() {
        Category category = Category.builder()
                .name("Laptop")
                .slug("laptop")
                .status("ACTIVE")
                .build();
        category.setId(10L);
        Brand brand = Brand.builder()
                .name("Apple")
                .slug("apple")
                .status("ACTIVE")
                .build();
        brand.setId(20L);

        Product product = Product.builder()
                .name("MacBook Pro M3")
                .slug("macbook-pro-m3")
                .description("Apple Silicon laptop")
                .category(category)
                .brand(brand)
                .status(ProductStatus.ACTIVE)
                .variants(new ArrayList<>())
                .attributes(new ArrayList<>())
                .images(new ArrayList<>())
                .build();
        product.setId(100L);
        product.setCreatedAt(Instant.parse("2026-01-01T00:00:00Z"));
        product.addVariant(ProductVariant.builder()
                .sku("MBP-M3-16-512")
                .name("16GB RAM / 512GB SSD")
                .price(new BigDecimal("39990000"))
                .originalPrice(new BigDecimal("41990000"))
                .stock(4)
                .status("ACTIVE")
                .build());
        product.addAttribute(ProductAttribute.builder().name("RAM").value("16GB").build());
        product.addImage(ProductImage.builder()
                .url("https://example.test/macbook.png")
                .isThumbnail(true)
                .sortOrder(0)
                .build());

        ProductSearchDocument document = ProductSearchDocument.from(product);

        assertEquals(100L, document.getId());
        assertEquals("RAM 16GB", document.getAttributesText());
        assertEquals(new BigDecimal("39990000"), document.getMinPrice());
        assertEquals(new BigDecimal("39990000"), document.getMaxPrice());
        assertEquals(4, document.getTotalStock());
        assertEquals("https://example.test/macbook.png", document.getThumbnailUrl());
        assertEquals("Apple", document.getBrandName());
        assertEquals("16GB RAM / 512GB SSD", document.getVariants().get(0).getName());

        var response = document.toResponse();
        assertEquals("MacBook Pro M3", response.getName());
        assertEquals("Laptop", response.getCategoryName());
        assertEquals("MBP-M3-16-512", response.getVariants().get(0).getSku());
    }
}

package com.project.techstore.product.search;

import com.project.techstore.product.dto.ProductResponse;
import com.project.techstore.product.dto.ProductVariantDto;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductImage;
import com.project.techstore.product.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "products-v1", createIndex = false)
@Setting(settingPath = "elasticsearch/product-search-settings.json")
public class ProductSearchDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long productId;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String name;

    @Field(type = FieldType.Keyword)
    private String nameSort;

    @Field(type = FieldType.Keyword)
    private String slug;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String description;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String attributesText;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String categoryName;

    @Field(type = FieldType.Keyword)
    private String categorySlug;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String brandName;

    @Field(type = FieldType.Keyword)
    private String thumbnailUrl;

    @Field(type = FieldType.Double)
    private BigDecimal minPrice;

    @Field(type = FieldType.Double)
    private BigDecimal maxPrice;

    @Field(type = FieldType.Integer)
    private Integer totalStock;

    @Field(type = FieldType.Keyword)
    private String status;

    @Field(type = FieldType.Nested)
    private List<ProductSearchVariant> variants;

    @Field(type = FieldType.Date)
    private Instant createdAt;

    public static ProductSearchDocument from(Product product) {
        List<ProductVariant> sourceVariants = product.getVariants() == null
                ? List.of()
                : product.getVariants();
        List<ProductSearchVariant> variants = sourceVariants.stream()
                .map(ProductSearchVariant::from)
                .toList();

        BigDecimal minPrice = sourceVariants.stream()
                .map(ProductVariant::getPrice)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = sourceVariants.stream()
                .map(ProductVariant::getPrice)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        int totalStock = sourceVariants.stream()
                .map(ProductVariant::getStock)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        String thumbnailUrl = null;
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            thumbnailUrl = product.getImages().stream()
                    .filter(image -> Boolean.TRUE.equals(image.getIsThumbnail()))
                    .map(ProductImage::getUrl)
                    .findFirst()
                    .orElseGet(() -> product.getImages().get(0).getUrl());
        }

        String attributesText = product.getAttributes() == null
                ? ""
                : product.getAttributes().stream()
                        .map(attribute -> attribute.getName() + " " + attribute.getValue())
                        .reduce((left, right) -> left + " " + right)
                        .orElse("");

        return ProductSearchDocument.builder()
                .id(product.getId())
                .productId(product.getId())
                .name(product.getName())
                .nameSort(product.getName())
                .slug(product.getSlug())
                .description(product.getDescription())
                .attributesText(attributesText)
                .categoryId(product.getCategory() == null ? null : product.getCategory().getId())
                .categoryName(product.getCategory() == null ? null : product.getCategory().getName())
                .categorySlug(product.getCategory() == null ? null : product.getCategory().getSlug())
                .brandId(product.getBrand() == null ? null : product.getBrand().getId())
                .brandName(product.getBrand() == null ? null : product.getBrand().getName())
                .thumbnailUrl(thumbnailUrl)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock)
                .status(product.getStatus() == null ? null : product.getStatus().name())
                .variants(variants)
                .createdAt(product.getCreatedAt())
                .build();
    }

    public ProductResponse toResponse() {
        List<ProductVariantDto> variantDtos = variants == null
                ? List.of()
                : variants.stream().map(ProductSearchVariant::toDto).toList();

        return ProductResponse.builder()
                .id(id)
                .name(name)
                .slug(slug)
                .categoryId(categoryId)
                .categoryName(categoryName)
                .categorySlug(categorySlug)
                .brandId(brandId)
                .brandName(brandName)
                .thumbnailUrl(thumbnailUrl)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .totalStock(totalStock)
                .status(status)
                .variants(variantDtos)
                .createdAt(createdAt)
                .build();
    }
}

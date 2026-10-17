package com.project.techstore.product.search;

import com.project.techstore.product.dto.ProductVariantDto;
import com.project.techstore.product.entity.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchVariant {

    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String sku;

    @Field(type = FieldType.Text, analyzer = "product_text", searchAnalyzer = "product_text")
    private String name;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Double)
    private BigDecimal originalPrice;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Keyword)
    private String status;

    public static ProductSearchVariant from(ProductVariant variant) {
        return ProductSearchVariant.builder()
                .id(variant.getId())
                .sku(variant.getSku())
                .name(variant.getName())
                .price(variant.getPrice())
                .originalPrice(variant.getOriginalPrice())
                .stock(variant.getStock())
                .status(variant.getStatus())
                .build();
    }

    public ProductVariantDto toDto() {
        return ProductVariantDto.builder()
                .id(id)
                .sku(sku)
                .name(name)
                .price(price)
                .originalPrice(originalPrice)
                .stock(stock)
                .status(status)
                .build();
    }
}

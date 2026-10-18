package com.project.techstore.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.dto.ProductStorefrontContentDto;
import com.project.techstore.product.entity.ProductStorefrontContent;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.product.repository.ProductStorefrontContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStorefrontContentService {

    private final ProductRepository productRepository;
    private final ProductStorefrontContentRepository contentRepository;
    private final ObjectMapper objectMapper;

    @Transactional(readOnly = true)
    public ProductStorefrontContentDto get(Long productId) {
        ensureProductExists(productId);
        return contentRepository.findByProductId(productId)
                .map(content -> normalize(objectMapper.convertValue(content.getContent(), ProductStorefrontContentDto.class)))
                .orElseGet(ProductStorefrontContentDto::new);
    }

    @Transactional
    public ProductStorefrontContentDto update(Long productId, ProductStorefrontContentDto request) {
        var product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND,
                        "Không tìm thấy sản phẩm với ID: " + productId));

        ProductStorefrontContent content = contentRepository.findByProductId(productId)
                .orElseGet(() -> ProductStorefrontContent.builder().product(product).build());
        ProductStorefrontContentDto normalizedRequest = normalize(request);
        content.setContent(objectMapper.valueToTree(normalizedRequest));
        ProductStorefrontContent saved = contentRepository.save(content);
        return normalize(objectMapper.convertValue(saved.getContent(), ProductStorefrontContentDto.class));
    }

    private ProductStorefrontContentDto normalize(ProductStorefrontContentDto content) {
        if (content == null) content = new ProductStorefrontContentDto();
        if (content.getStorySections() == null) content.setStorySections(new java.util.ArrayList<>());
        if (content.getMedia() == null) content.setMedia(new ProductStorefrontContentDto.Media());
        if (content.getPromotions() == null) content.setPromotions(new java.util.ArrayList<>());
        if (content.getPaymentOffers() == null) content.setPaymentOffers(new java.util.ArrayList<>());
        if (content.getBundleOffers() == null) content.setBundleOffers(new java.util.ArrayList<>());
        if (content.getFaqs() == null) content.setFaqs(new java.util.ArrayList<>());
        if (content.getCityAvailability() == null) content.setCityAvailability(new java.util.ArrayList<>());
        if (content.getInstallmentPlans() == null) content.setInstallmentPlans(new java.util.ArrayList<>());
        if (content.getTradeInOffers() == null) content.setTradeInOffers(new java.util.ArrayList<>());
        if (content.getStorePolicy() == null) content.setStorePolicy(new ProductStorefrontContentDto.StorePolicy());
        if (content.getComparisonProductIds() == null) content.setComparisonProductIds(new java.util.ArrayList<>());
        return content;
    }

    private void ensureProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new AppException(ErrorCode.PRODUCT_NOT_FOUND,
                    "Không tìm thấy sản phẩm với ID: " + productId);
        }
    }
}

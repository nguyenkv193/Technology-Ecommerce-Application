package com.project.techstore.product.service;

import com.project.techstore.brand.entity.Brand;
import com.project.techstore.brand.service.BrandService;
import com.project.techstore.category.entity.Category;
import com.project.techstore.category.service.CategoryService;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.dto.*;
import com.project.techstore.product.entity.*;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.product.repository.ProductVariantRepository;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CategoryService categoryService;
    private final BrandService brandService;

    @Transactional(readOnly = true)
    public PageResponse<ProductRecommendationFeature> getRecommendationFeatures(int page, int size) {
        return PageResponse.of(productRepository.findByStatus(ProductStatus.ACTIVE,
                PageRequest.of(page, size, Sort.by("id"))).map(ProductRecommendationFeature::from));
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> filterProducts(ProductFilterRequest filter) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Lọc theo trạng thái (mặc định ACTIVE cho khách, hoặc theo filter)
            if (StringUtils.hasText(filter.getStatus())) {
                try {
                    ProductStatus status = ProductStatus.valueOf(filter.getStatus().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), status));
                } catch (IllegalArgumentException ignored) {}
            } else {
                predicates.add(cb.equal(root.get("status"), ProductStatus.ACTIVE));
            }

            // 2. Tìm kiếm từ khóa theo tên hoặc mô tả
            if (StringUtils.hasText(filter.getKeyword())) {
                String kw = "%" + filter.getKeyword().trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), kw);
                Predicate descLike = cb.like(cb.lower(root.get("description")), kw);
                predicates.add(cb.or(nameLike, descLike));
            }

            // 3. Lọc theo Category ID
            if (filter.getCategoryId() != null) {
                predicates.add(cb.equal(root.get("category").get("id"), filter.getCategoryId()));
            }

            // 4. Lọc theo Brand ID
            if (filter.getBrandId() != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), filter.getBrandId()));
            }

            // 5. Lọc theo khoảng giá (minPrice, maxPrice) qua bảng product_variants bằng Subquery
            if (filter.getMinPrice() != null || filter.getMaxPrice() != null) {
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<ProductVariant> variantRoot = subquery.from(ProductVariant.class);
                subquery.select(variantRoot.get("product").get("id"));

                List<Predicate> subPredicates = new ArrayList<>();
                if (filter.getMinPrice() != null) {
                    subPredicates.add(cb.greaterThanOrEqualTo(variantRoot.get("price"), filter.getMinPrice()));
                }
                if (filter.getMaxPrice() != null) {
                    subPredicates.add(cb.lessThanOrEqualTo(variantRoot.get("price"), filter.getMaxPrice()));
                }
                subquery.where(subPredicates.toArray(new Predicate[0]));

                predicates.add(root.get("id").in(subquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort.Direction direction = "ASC".equalsIgnoreCase(filter.getSortDirection())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        String sortBy = StringUtils.hasText(filter.getSortBy()) ? filter.getSortBy() : "createdAt";

        Pageable pageable = PageRequest.of(
                Math.max(0, filter.getPage()),
                Math.max(1, filter.getSize()),
                Sort.by(direction, sortBy)
        );

        Page<Product> page = productRepository.findAll(spec, pageable);
        return PageResponse.of(page.map(ProductResponse::from));
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getById(Long id) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));
        return ProductDetailResponse.from(product);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponse getBySlug(String slug) {
        Product product = productRepository.findWithDetailsBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm với slug: " + slug));
        return ProductDetailResponse.from(product);
    }

    @Transactional
    public ProductDetailResponse create(ProductCreateRequest request) {
        Category category = categoryService.getCategoryEntity(request.getCategoryId());
        Brand brand = brandService.getBrandEntity(request.getBrandId());

        String slug = StringUtils.hasText(request.getSlug())
                ? CategoryService.toSlug(request.getSlug())
                : CategoryService.toSlug(request.getName());

        if (productRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Product product = Product.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .category(category)
                .brand(brand)
                .status(request.getStatus() != null ? request.getStatus() : ProductStatus.ACTIVE)
                .variants(new ArrayList<>())
                .attributes(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        // Thêm biến thể SKU
        for (int i = 0; i < request.getVariants().size(); i++) {
            ProductVariantDto vDto = request.getVariants().get(i);
            String sku = StringUtils.hasText(vDto.getSku())
                    ? vDto.getSku().trim().toUpperCase()
                    : slug.toUpperCase() + "-SKU-" + (i + 1);

            if (productVariantRepository.existsBySku(sku)) {
                throw new AppException(ErrorCode.CONFLICT, "Mã SKU đã tồn tại trong hệ thống: " + sku);
            }

            ProductVariant variant = ProductVariant.builder()
                    .sku(sku)
                    .name(vDto.getName().trim())
                    .price(vDto.getPrice())
                    .originalPrice(vDto.getOriginalPrice())
                    .stock(vDto.getStock() != null ? vDto.getStock() : 0)
                    .status(StringUtils.hasText(vDto.getStatus()) ? vDto.getStatus() : "ACTIVE")
                    .build();
            product.addVariant(variant);
        }

        // Thêm thông số kỹ thuật động (Dynamic Attributes cho AI & Filter)
        if (request.getAttributes() != null) {
            for (ProductAttributeDto aDto : request.getAttributes()) {
                ProductAttribute attr = ProductAttribute.builder()
                        .name(aDto.getName().trim())
                        .value(aDto.getValue().trim())
                        .build();
                product.addAttribute(attr);
            }
        }

        // Thêm danh sách ảnh
        if (request.getImages() != null) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ProductImageDto imgDto = request.getImages().get(i);
                ProductImage img = ProductImage.builder()
                        .url(imgDto.getUrl().trim())
                        .sortOrder(imgDto.getSortOrder() != null ? imgDto.getSortOrder() : i)
                        .isThumbnail(Boolean.TRUE.equals(imgDto.getIsThumbnail()))
                        .build();
                product.addImage(img);
            }
        }

        Product saved = productRepository.save(product);
        log.info("Đã tạo mới sản phẩm công nghệ: {} (id={}, variants={})", saved.getName(), saved.getId(), saved.getVariants().size());
        return ProductDetailResponse.from(saved);
    }

    @Transactional
    public ProductDetailResponse update(Long id, ProductUpdateRequest request) {
        Product product = productRepository.findWithDetailsById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));

        Category category = categoryService.getCategoryEntity(request.getCategoryId());
        Brand brand = brandService.getBrandEntity(request.getBrandId());

        product.setName(request.getName().trim());
        product.setDescription(request.getDescription());
        product.setCategory(category);
        product.setBrand(brand);
        if (request.getStatus() != null) {
            product.setStatus(request.getStatus());
        }

        if (StringUtils.hasText(request.getSlug()) && !request.getSlug().equalsIgnoreCase(product.getSlug())) {
            String newSlug = CategoryService.toSlug(request.getSlug());
            if (productRepository.existsBySlug(newSlug)) {
                newSlug = newSlug + "-" + System.currentTimeMillis();
            }
            product.setSlug(newSlug);
        }

        // Cập nhật thông số kỹ thuật
        product.getAttributes().clear();
        if (request.getAttributes() != null) {
            for (ProductAttributeDto aDto : request.getAttributes()) {
                product.addAttribute(ProductAttribute.builder()
                        .name(aDto.getName().trim())
                        .value(aDto.getValue().trim())
                        .build());
            }
        }

        // Cập nhật hình ảnh
        product.getImages().clear();
        if (request.getImages() != null) {
            for (int i = 0; i < request.getImages().size(); i++) {
                ProductImageDto imgDto = request.getImages().get(i);
                product.addImage(ProductImage.builder()
                        .url(imgDto.getUrl().trim())
                        .sortOrder(imgDto.getSortOrder() != null ? imgDto.getSortOrder() : i)
                        .isThumbnail(Boolean.TRUE.equals(imgDto.getIsThumbnail()))
                        .build());
            }
        }

        // Cập nhật biến thể SKU
        product.getVariants().clear();
        for (int i = 0; i < request.getVariants().size(); i++) {
            ProductVariantDto vDto = request.getVariants().get(i);
            String sku = StringUtils.hasText(vDto.getSku())
                    ? vDto.getSku().trim().toUpperCase()
                    : product.getSlug().toUpperCase() + "-SKU-" + (i + 1);

            product.addVariant(ProductVariant.builder()
                    .sku(sku)
                    .name(vDto.getName().trim())
                    .price(vDto.getPrice())
                    .originalPrice(vDto.getOriginalPrice())
                    .stock(vDto.getStock() != null ? vDto.getStock() : 0)
                    .status(StringUtils.hasText(vDto.getStatus()) ? vDto.getStatus() : "ACTIVE")
                    .build());
        }

        Product updated = productRepository.save(product);
        log.info("Đã cập nhật sản phẩm công nghệ id={}", id);
        return ProductDetailResponse.from(updated);
    }

    @Transactional
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));
        productRepository.delete(product);
        log.info("Đã xóa sản phẩm id={}", id);
    }

    @Transactional(readOnly = true)
    public Product getProductEntity(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRODUCT_NOT_FOUND, "Không tìm thấy sản phẩm với ID: " + id));
    }
}

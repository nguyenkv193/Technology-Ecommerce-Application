package com.project.techstore.brand.service;

import com.project.techstore.brand.dto.BrandRequest;
import com.project.techstore.brand.dto.BrandResponse;
import com.project.techstore.brand.entity.Brand;
import com.project.techstore.brand.repository.BrandRepository;
import com.project.techstore.category.service.CategoryService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrandService {

    private final BrandRepository brandRepository;

    @Transactional(readOnly = true)
    public List<BrandResponse> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(BrandResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BrandResponse getById(Long id) {
        return BrandResponse.from(getBrandEntity(id));
    }

    @Transactional(readOnly = true)
    public BrandResponse getBySlug(String slug) {
        Brand brand = brandRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND, "Không tìm thấy thương hiệu với slug: " + slug));
        return BrandResponse.from(brand);
    }

    @Transactional
    public BrandResponse create(BrandRequest request) {
        if (brandRepository.existsByName(request.getName().trim())) {
            throw new AppException(ErrorCode.CONFLICT, "Tên thương hiệu đã tồn tại: " + request.getName());
        }

        String slug = StringUtils.hasText(request.getSlug())
                ? CategoryService.toSlug(request.getSlug())
                : CategoryService.toSlug(request.getName());

        if (brandRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Brand brand = Brand.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .logoUrl(request.getLogoUrl())
                .websiteUrl(request.getWebsiteUrl())
                .status("ACTIVE")
                .build();

        Brand saved = brandRepository.save(brand);
        log.info("Đã tạo mới thương hiệu: {} (id={})", saved.getName(), saved.getId());
        return BrandResponse.from(saved);
    }

    @Transactional
    public BrandResponse update(Long id, BrandRequest request) {
        Brand brand = getBrandEntity(id);

        if (StringUtils.hasText(request.getName()) && !brand.getName().equalsIgnoreCase(request.getName().trim())) {
            if (brandRepository.existsByName(request.getName().trim())) {
                throw new AppException(ErrorCode.CONFLICT, "Tên thương hiệu đã tồn tại: " + request.getName());
            }
            brand.setName(request.getName().trim());
        }

        if (StringUtils.hasText(request.getDescription())) {
            brand.setDescription(request.getDescription());
        }
        if (request.getLogoUrl() != null) {
            brand.setLogoUrl(request.getLogoUrl());
        }
        if (request.getWebsiteUrl() != null) {
            brand.setWebsiteUrl(request.getWebsiteUrl());
        }

        Brand updated = brandRepository.save(brand);
        log.info("Đã cập nhật thương hiệu id={}", id);
        return BrandResponse.from(updated);
    }

    @Transactional
    public void delete(Long id) {
        Brand brand = getBrandEntity(id);
        brandRepository.delete(brand);
        log.info("Đã xóa thương hiệu id={}", id);
    }

    @Transactional(readOnly = true)
    public Brand getBrandEntity(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.BRAND_NOT_FOUND, "Không tìm thấy thương hiệu với ID: " + id));
    }
}

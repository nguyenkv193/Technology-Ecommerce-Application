package com.project.techstore.category.service;

import com.project.techstore.category.dto.CategoryRequest;
import com.project.techstore.category.dto.CategoryResponse;
import com.project.techstore.category.entity.Category;
import com.project.techstore.category.repository.CategoryRepository;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllRootCategories() {
        return categoryRepository.findByParentIsNullOrderByCreatedAtDesc()
                .stream()
                .map(cat -> CategoryResponse.from(cat, true))
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Long id) {
        Category category = getCategoryEntity(id);
        return CategoryResponse.from(category, true);
    }

    @Transactional(readOnly = true)
    public CategoryResponse getBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Không tìm thấy danh mục với slug: " + slug));
        return CategoryResponse.from(category, true);
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String slug = StringUtils.hasText(request.getSlug()) ? toSlug(request.getSlug()) : toSlug(request.getName());

        if (categoryRepository.existsBySlug(slug)) {
            slug = slug + "-" + System.currentTimeMillis();
        }

        Category parent = null;
        if (request.getParentId() != null) {
            parent = getCategoryEntity(request.getParentId());
        }

        Category category = Category.builder()
                .name(request.getName().trim())
                .slug(slug)
                .description(request.getDescription())
                .parent(parent)
                .imageUrl(request.getImageUrl())
                .status("ACTIVE")
                .build();

        Category saved = categoryRepository.save(category);
        log.info("Đã tạo mới danh mục: {} (id={})", saved.getName(), saved.getId());
        return CategoryResponse.from(saved, false);
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = getCategoryEntity(id);

        if (StringUtils.hasText(request.getName())) {
            category.setName(request.getName().trim());
        }
        if (StringUtils.hasText(request.getDescription())) {
            category.setDescription(request.getDescription());
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getParentId() != null) {
            if (request.getParentId().equals(id)) {
                throw new AppException(ErrorCode.INVALID_REQUEST, "Danh mục không thể là cha của chính nó");
            }
            Category parent = getCategoryEntity(request.getParentId());
            category.setParent(parent);
        }

        Category updated = categoryRepository.save(category);
        log.info("Đã cập nhật danh mục id={}", id);
        return CategoryResponse.from(updated, true);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getCategoryEntity(id);
        categoryRepository.delete(category);
        log.info("Đã xóa danh mục id={}", id);
    }

    @Transactional(readOnly = true)
    public Category getCategoryEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND, "Không tìm thấy danh mục với ID: " + id));
    }

    public static String toSlug(String input) {
        if (!StringUtils.hasText(input)) return "";
        String nowhitespace = Pattern.compile("[\\s]").matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = Pattern.compile("[^\\w-]").matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH).replaceAll("-+", "-");
    }
}

package com.project.techstore.category;

import com.project.techstore.category.dto.CategoryRequest;
import com.project.techstore.category.dto.CategoryResponse;
import com.project.techstore.category.entity.Category;
import com.project.techstore.category.repository.CategoryRepository;
import com.project.techstore.category.service.CategoryService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .name("Laptop & Máy tính")
                .slug("laptop-may-tinh")
                .description("Các loại máy tính xách tay")
                .status("ACTIVE")
                .subCategories(new ArrayList<>())
                .build();
        parentCategory.setId(1L);

        childCategory = Category.builder()
                .name("MacBook")
                .slug("macbook")
                .parent(parentCategory)
                .status("ACTIVE")
                .subCategories(new ArrayList<>())
                .build();
        childCategory.setId(2L);
    }

    @Test
    void testGetAllRootCategories() {
        when(categoryRepository.findByParentIsNullOrderByCreatedAtDesc()).thenReturn(List.of(parentCategory));

        List<CategoryResponse> result = categoryService.getAllRootCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Laptop & Máy tính", result.get(0).getName());
        verify(categoryRepository).findByParentIsNullOrderByCreatedAtDesc();
    }

    @Test
    void testGetByIdSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        CategoryResponse result = categoryService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("laptop-may-tinh", result.getSlug());
    }

    @Test
    void testGetByIdNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> categoryService.getById(99L));
        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testGetBySlugSuccess() {
        when(categoryRepository.findBySlug("laptop-may-tinh")).thenReturn(Optional.of(parentCategory));

        CategoryResponse result = categoryService.getBySlug("laptop-may-tinh");

        assertNotNull(result);
        assertEquals("Laptop & Máy tính", result.getName());
    }

    @Test
    void testGetBySlugNotFound() {
        when(categoryRepository.findBySlug("unknown")).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> categoryService.getBySlug("unknown"));
        assertEquals(ErrorCode.CATEGORY_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateWithoutParentAndAutoSlug() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Điện thoại thông minh")
                .description("Smartphones")
                .build();

        when(categoryRepository.existsBySlug("dien-thoai-thong-minh")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        CategoryResponse result = categoryService.create(request);

        assertNotNull(result);
        assertEquals("Điện thoại thông minh", result.getName());
        assertEquals("dien-thoai-thong-minh", result.getSlug());
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testCreateWithDuplicateSlugAppendsTimestamp() {
        CategoryRequest request = CategoryRequest.builder()
                .name("MacBook")
                .slug("macbook")
                .build();

        when(categoryRepository.existsBySlug("macbook")).thenReturn(true);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });

        CategoryResponse result = categoryService.create(request);

        assertNotNull(result);
        assertTrue(result.getSlug().startsWith("macbook-"));
    }

    @Test
    void testCreateWithParent() {
        CategoryRequest request = CategoryRequest.builder()
                .name("MacBook Pro")
                .parentId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.existsBySlug("macbook-pro")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> {
            Category saved = invocation.getArgument(0);
            saved.setId(12L);
            return saved;
        });

        CategoryResponse result = categoryService.create(request);

        assertNotNull(result);
        verify(categoryRepository).findById(1L);
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void testUpdateSuccess() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Laptop & Ultrabook")
                .description("Updated desc")
                .imageUrl("https://example.com/laptop.jpg")
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CategoryResponse result = categoryService.update(1L, request);

        assertNotNull(result);
        assertEquals("Laptop & Ultrabook", result.getName());
        assertEquals("Updated desc", result.getDescription());
        assertEquals("https://example.com/laptop.jpg", result.getImageUrl());
    }

    @Test
    void testUpdateCannotSetSelfAsParent() {
        CategoryRequest request = CategoryRequest.builder()
                .name("Laptop")
                .parentId(1L)
                .build();

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        AppException ex = assertThrows(AppException.class, () -> categoryService.update(1L, request));
        assertEquals(ErrorCode.INVALID_REQUEST, ex.getErrorCode());
    }

    @Test
    void testDeleteSuccess() {
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(childCategory));

        categoryService.delete(2L);

        verify(categoryRepository).delete(childCategory);
    }

    @Test
    void testToSlugNormalization() {
        assertEquals("dien-thoai-thong-minh", CategoryService.toSlug("Điện thoại thông minh"));
        assertEquals("may-tinh-bang", CategoryService.toSlug("  Máy tính bảng  "));
        assertEquals("", CategoryService.toSlug(null));
        assertEquals("", CategoryService.toSlug("   "));
    }
}

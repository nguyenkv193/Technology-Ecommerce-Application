package com.project.techstore.product;

import com.project.techstore.brand.entity.Brand;
import com.project.techstore.brand.service.BrandService;
import com.project.techstore.category.entity.Category;
import com.project.techstore.category.service.CategoryService;
import com.project.techstore.common.exception.AppException;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.product.dto.ProductAttributeDto;
import com.project.techstore.product.dto.ProductCreateRequest;
import com.project.techstore.product.dto.ProductDetailResponse;
import com.project.techstore.product.dto.ProductVariantDto;
import com.project.techstore.product.entity.Product;
import com.project.techstore.product.entity.ProductStatus;
import com.project.techstore.product.repository.ProductRepository;
import com.project.techstore.product.repository.ProductVariantRepository;
import com.project.techstore.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductVariantRepository productVariantRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BrandService brandService;

    @InjectMocks
    private ProductService productService;

    private Category sampleCategory;
    private Brand sampleBrand;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleCategory = Category.builder().name("Laptop").slug("laptop").status("ACTIVE").build();
        sampleCategory.setId(10L);

        sampleBrand = Brand.builder().name("Apple").slug("apple").status("ACTIVE").build();
        sampleBrand.setId(20L);

        sampleProduct = Product.builder()
                .name("MacBook Pro M3")
                .slug("macbook-pro-m3")
                .description("Laptop Apple Silicon")
                .category(sampleCategory)
                .brand(sampleBrand)
                .status(ProductStatus.ACTIVE)
                .variants(new ArrayList<>())
                .attributes(new ArrayList<>())
                .images(new ArrayList<>())
                .build();
        sampleProduct.setId(100L);
    }

    @Test
    void testGetByIdSuccess() {
        when(productRepository.findWithDetailsById(100L)).thenReturn(Optional.of(sampleProduct));

        ProductDetailResponse res = productService.getById(100L);

        assertNotNull(res);
        assertEquals(100L, res.getId());
        assertEquals("MacBook Pro M3", res.getName());
        assertEquals("laptop", res.getCategory().getSlug());
        assertEquals("Apple", res.getBrand().getName());
    }

    @Test
    void testGetByIdNotFound() {
        when(productRepository.findWithDetailsById(999L)).thenReturn(Optional.empty());

        AppException ex = assertThrows(AppException.class, () -> productService.getById(999L));
        assertEquals(ErrorCode.PRODUCT_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void testCreateProductSuccess() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("MacBook Pro M3")
                .categoryId(10L)
                .brandId(20L)
                .description("Laptop Apple Silicon")
                .variants(List.of(
                        ProductVariantDto.builder()
                                .sku("MBP-M3-16-512")
                                .name("16GB RAM / 512GB SSD")
                                .price(new BigDecimal("39990000"))
                                .stock(20)
                                .build()
                ))
                .attributes(List.of(
                        ProductAttributeDto.builder()
                                .name("RAM")
                                .value("16GB")
                                .build(),
                        ProductAttributeDto.builder()
                                .name("CPU")
                                .value("Apple M3")
                                .build()
                ))
                .build();

        when(categoryService.getCategoryEntity(10L)).thenReturn(sampleCategory);
        when(brandService.getBrandEntity(20L)).thenReturn(sampleBrand);
        when(productRepository.existsBySlug("macbook-pro-m3")).thenReturn(false);
        when(productVariantRepository.existsBySku("MBP-M3-16-512")).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(100L);
            return p;
        });

        ProductDetailResponse res = productService.create(request);

        assertNotNull(res);
        assertEquals("MacBook Pro M3", res.getName());
        assertEquals("macbook-pro-m3", res.getSlug());
        assertEquals(1, res.getVariants().size());
        assertEquals(2, res.getAttributes().size());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProductDuplicateSkuThrowsException() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("MacBook Pro M3")
                .categoryId(10L)
                .brandId(20L)
                .variants(List.of(
                        ProductVariantDto.builder()
                                .sku("DUPLICATE-SKU")
                                .name("16GB RAM")
                                .price(new BigDecimal("39990000"))
                                .stock(10)
                                .build()
                ))
                .build();

        when(categoryService.getCategoryEntity(10L)).thenReturn(sampleCategory);
        when(brandService.getBrandEntity(20L)).thenReturn(sampleBrand);
        when(productVariantRepository.existsBySku("DUPLICATE-SKU")).thenReturn(true);

        AppException ex = assertThrows(AppException.class, () -> productService.create(request));
        assertEquals(ErrorCode.CONFLICT, ex.getErrorCode());
        verify(productRepository, never()).save(any(Product.class));
    }
}

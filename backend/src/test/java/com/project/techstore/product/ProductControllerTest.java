package com.project.techstore.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.common.dto.PageResponse;
import com.project.techstore.product.controller.ProductController;
import com.project.techstore.product.dto.*;
import com.project.techstore.product.entity.ProductStatus;
import com.project.techstore.product.service.ProductService;
import com.project.techstore.recommendation.service.RecommendationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private RecommendationService recommendationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private ProductResponse sampleProductResponse;
    private ProductDetailResponse sampleDetailResponse;

    @BeforeEach
    void setUp() {
        sampleProductResponse = ProductResponse.builder()
                .id(1L)
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .categoryName("Laptop")
                .brandName("Apple")
                .minPrice(BigDecimal.valueOf(79990000))
                .status("ACTIVE")
                .build();

        sampleDetailResponse = ProductDetailResponse.builder()
                .id(1L)
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .description("M3 Max power")
                .status("ACTIVE")
                .variants(List.of())
                .attributes(List.of())
                .images(List.of())
                .build();
    }

    @Test
    void testBestSellers() throws Exception {
        when(recommendationService.getBestSellers(8)).thenReturn(List.of(sampleProductResponse));

        mockMvc.perform(get("/api/v1/products/best-sellers?limit=8"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("MacBook Pro 16"));

        verify(recommendationService).getBestSellers(8);
    }

    @Test
    void testRecommendationFeatures() throws Exception {
        PageResponse<ProductRecommendationFeature> pageResponse = PageResponse.<ProductRecommendationFeature>builder()
                .content(List.of())
                .page(0)
                .size(100)
                .totalElements(0)
                .totalPages(0)
                .last(true)
                .build();

        when(productService.getRecommendationFeatures(0, 100)).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/products/recommendation-features?page=0&size=100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(productService).getRecommendationFeatures(0, 100);
    }

    @Test
    void testFilterProducts() throws Exception {
        PageResponse<ProductResponse> pageResponse = PageResponse.<ProductResponse>builder()
                .content(List.of(sampleProductResponse))
                .page(0)
                .size(10)
                .totalElements(1)
                .totalPages(1)
                .last(true)
                .build();

        when(productService.filterProducts(any(ProductFilterRequest.class))).thenReturn(pageResponse);

        mockMvc.perform(get("/api/v1/products?page=0&size=10&keyword=macbook"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].name").value("MacBook Pro 16"));

        verify(productService).filterProducts(any(ProductFilterRequest.class));
    }

    @Test
    void testSearchSuggestions() throws Exception {
        ProductSuggestionResponse suggestion = ProductSuggestionResponse.builder()
                .id(1L)
                .name("MacBook Pro 16")
                .slug("macbook-pro-16")
                .minPrice(BigDecimal.valueOf(79990000))
                .build();

        when(productService.getSearchSuggestions("macbook", 6)).thenReturn(List.of(suggestion));

        mockMvc.perform(get("/api/v1/products/suggestions?keyword=macbook&limit=6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("MacBook Pro 16"));

        verify(productService).getSearchSuggestions("macbook", 6);
    }

    @Test
    void testGetProductById() throws Exception {
        when(productService.getById(1L)).thenReturn(sampleDetailResponse);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));

        verify(productService).getById(1L);
    }

    @Test
    void testGetProductBySlug() throws Exception {
        when(productService.getBySlug("macbook-pro-16")).thenReturn(sampleDetailResponse);

        mockMvc.perform(get("/api/v1/products/slug/macbook-pro-16"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("macbook-pro-16"));

        verify(productService).getBySlug("macbook-pro-16");
    }

    @Test
    void testCreateProductSuccess() throws Exception {
        ProductVariantDto variant = ProductVariantDto.builder()
                .sku("MBP-01")
                .name("Default")
                .price(BigDecimal.valueOf(79990000))
                .stock(10)
                .build();

        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("MacBook Pro 16")
                .categoryId(1L)
                .brandId(1L)
                .variants(List.of(variant))
                .build();

        when(productService.create(any(ProductCreateRequest.class))).thenReturn(sampleDetailResponse);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tạo sản phẩm thành công"));

        verify(productService).create(any(ProductCreateRequest.class));
    }

    @Test
    void testCreateProductValidationFailed() throws Exception {
        ProductCreateRequest invalidRequest = ProductCreateRequest.builder()
                .name("") // blank name
                .variants(List.of()) // empty variants
                .build();

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(productService);
    }

    @Test
    void testUpdateProductSuccess() throws Exception {
        ProductVariantDto variant = ProductVariantDto.builder()
                .sku("MBP-01")
                .name("Default")
                .price(BigDecimal.valueOf(79990000))
                .stock(10)
                .build();

        ProductUpdateRequest request = ProductUpdateRequest.builder()
                .name("MacBook Pro 16 M3 Max")
                .categoryId(1L)
                .brandId(1L)
                .variants(List.of(variant))
                .build();

        when(productService.update(eq(1L), any(ProductUpdateRequest.class))).thenReturn(sampleDetailResponse);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cập nhật sản phẩm thành công"));

        verify(productService).update(eq(1L), any(ProductUpdateRequest.class));
    }

    @Test
    void testDeleteProductSuccess() throws Exception {
        doNothing().when(productService).delete(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã xóa sản phẩm thành công"));

        verify(productService).delete(1L);
    }
}

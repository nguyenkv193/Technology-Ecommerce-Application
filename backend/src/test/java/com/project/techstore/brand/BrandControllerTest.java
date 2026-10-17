package com.project.techstore.brand;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.brand.controller.BrandController;
import com.project.techstore.brand.dto.BrandRequest;
import com.project.techstore.brand.dto.BrandResponse;
import com.project.techstore.brand.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BrandController.class)
@AutoConfigureMockMvc(addFilters = false)
class BrandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BrandService brandService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private BrandResponse sampleBrand;

    @BeforeEach
    void setUp() {
        sampleBrand = BrandResponse.builder()
                .id(1L)
                .name("Apple")
                .slug("apple")
                .description("Apple Inc.")
                .logoUrl("https://img.techstore.vn/apple.png")
                .websiteUrl("https://apple.com")
                .status("ACTIVE")
                .build();
    }

    @Test
    void testGetAllBrands() throws Exception {
        when(brandService.getAllBrands()).thenReturn(List.of(sampleBrand));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Apple"))
                .andExpect(jsonPath("$.data[0].slug").value("apple"));

        verify(brandService).getAllBrands();
    }

    @Test
    void testGetBrandById() throws Exception {
        when(brandService.getById(1L)).thenReturn(sampleBrand);

        mockMvc.perform(get("/api/v1/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.name").value("Apple"));

        verify(brandService).getById(1L);
    }

    @Test
    void testGetBrandBySlug() throws Exception {
        when(brandService.getBySlug("apple")).thenReturn(sampleBrand);

        mockMvc.perform(get("/api/v1/brands/slug/apple"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("apple"));

        verify(brandService).getBySlug("apple");
    }

    @Test
    void testCreateBrandSuccess() throws Exception {
        BrandRequest request = BrandRequest.builder()
                .name("Apple")
                .slug("apple")
                .description("Apple Inc.")
                .build();

        when(brandService.create(any(BrandRequest.class))).thenReturn(sampleBrand);

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Tạo thương hiệu thành công"))
                .andExpect(jsonPath("$.data.name").value("Apple"));

        verify(brandService).create(any(BrandRequest.class));
    }

    @Test
    void testCreateBrandValidationFailed() throws Exception {
        BrandRequest invalidRequest = BrandRequest.builder()
                .name("")
                .build();

        mockMvc.perform(post("/api/v1/brands")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));

        verifyNoInteractions(brandService);
    }

    @Test
    void testUpdateBrandSuccess() throws Exception {
        BrandRequest request = BrandRequest.builder()
                .name("Apple Updated")
                .build();

        when(brandService.update(eq(1L), any(BrandRequest.class))).thenReturn(sampleBrand);

        mockMvc.perform(put("/api/v1/brands/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cập nhật thương hiệu thành công"));

        verify(brandService).update(eq(1L), any(BrandRequest.class));
    }

    @Test
    void testDeleteBrandSuccess() throws Exception {
        doNothing().when(brandService).delete(1L);

        mockMvc.perform(delete("/api/v1/brands/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã xóa thương hiệu thành công"));

        verify(brandService).delete(1L);
    }
}

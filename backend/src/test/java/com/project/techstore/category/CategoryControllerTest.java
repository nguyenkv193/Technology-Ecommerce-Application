package com.project.techstore.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.category.controller.CategoryController;
import com.project.techstore.category.dto.CategoryRequest;
import com.project.techstore.category.dto.CategoryResponse;
import com.project.techstore.category.service.CategoryService;
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

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testGetAllCategories() throws Exception {
        CategoryResponse cat = CategoryResponse.builder()
                .id(1L)
                .name("Laptop")
                .slug("laptop")
                .build();
        when(categoryService.getAllRootCategories()).thenReturn(List.of(cat));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].name").value("Laptop"));
    }

    @Test
    void testGetCategoryById() throws Exception {
        CategoryResponse cat = CategoryResponse.builder()
                .id(1L)
                .name("Laptop")
                .slug("laptop")
                .build();
        when(categoryService.getById(1L)).thenReturn(cat);

        mockMvc.perform(get("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.slug").value("laptop"));
    }

    @Test
    void testGetCategoryBySlug() throws Exception {
        CategoryResponse cat = CategoryResponse.builder()
                .id(1L)
                .name("Laptop")
                .slug("laptop")
                .build();
        when(categoryService.getBySlug("laptop")).thenReturn(cat);

        mockMvc.perform(get("/api/v1/categories/slug/laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void testCreateCategory() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Máy tính bảng")
                .description("Tablets")
                .build();

        CategoryResponse created = CategoryResponse.builder()
                .id(5L)
                .name("Máy tính bảng")
                .slug("may-tinh-bang")
                .build();

        when(categoryService.create(any(CategoryRequest.class))).thenReturn(created);

        mockMvc.perform(post("/api/v1/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(5))
                .andExpect(jsonPath("$.data.slug").value("may-tinh-bang"));
    }

    @Test
    void testUpdateCategory() throws Exception {
        CategoryRequest request = CategoryRequest.builder()
                .name("Laptop Gaming")
                .build();

        CategoryResponse updated = CategoryResponse.builder()
                .id(1L)
                .name("Laptop Gaming")
                .slug("laptop-gaming")
                .build();

        when(categoryService.update(eq(1L), any(CategoryRequest.class))).thenReturn(updated);

        mockMvc.perform(put("/api/v1/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Laptop Gaming"));
    }

    @Test
    void testDeleteCategory() throws Exception {
        doNothing().when(categoryService).delete(1L);

        mockMvc.perform(delete("/api/v1/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(categoryService).delete(1L);
    }
}

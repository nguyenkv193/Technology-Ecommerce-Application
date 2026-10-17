package com.project.techstore.product.search;

import com.project.techstore.auth.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductSearchAdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductSearchAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductSearchIndexService indexService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testReindexProducts() throws Exception {
        when(indexService.reindexAllProducts()).thenReturn(42L);

        mockMvc.perform(post("/api/v1/admin/search/reindex-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Đã tạo lại chỉ mục tìm kiếm sản phẩm"))
                .andExpect(jsonPath("$.data").value(42));

        verify(indexService).reindexAllProducts();
    }
}

package com.project.techstore;

import com.project.techstore.common.controller.HealthCheckController;
import com.project.techstore.common.exception.ErrorCode;
import com.project.techstore.common.response.ApiResponse;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthCheckController.class)
@AutoConfigureMockMvc(addFilters = false)
class TechstoreApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testApiResponseSuccessStructure() {
        ApiResponse<String> response = ApiResponse.success("Dữ liệu test");
        assertTrue(response.isSuccess());
        assertEquals("Dữ liệu test", response.getData());
        assertNotNull(response.getTimestamp());
        assertNull(response.getCode());
    }

    @Test
    void testApiResponseErrorStructure() {
        ApiResponse<Void> response = ApiResponse.error(ErrorCode.USER_NOT_FOUND.name(), "Không tìm thấy user");
        assertFalse(response.isSuccess());
        assertEquals(ErrorCode.USER_NOT_FOUND.name(), response.getCode());
        assertEquals("Không tìm thấy user", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void testHealthCheckEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("UP"))
                .andExpect(jsonPath("$.data.service").value("TechStore E-Commerce & AI Backend"));
    }
}

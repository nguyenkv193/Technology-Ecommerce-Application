package com.project.techstore.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.controller.AuthController;
import com.project.techstore.auth.dto.AuthResponse;
import com.project.techstore.auth.dto.LoginRequest;
import com.project.techstore.auth.dto.RefreshTokenRequest;
import com.project.techstore.auth.dto.RegisterRequest;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.auth.service.AuthService;
import com.project.techstore.user.dto.UserResponse;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void testRegister() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .email("newuser@techstore.vn")
                .password("Password123")
                .fullName("New User")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("access_token_123")
                .refreshToken("refresh_token_123")
                .tokenType("Bearer")
                .build();

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access_token_123"));
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("user@techstore.vn")
                .password("Password123")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("access_token_123")
                .refreshToken("refresh_token_123")
                .tokenType("Bearer")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access_token_123"));
    }

    @Test
    void testRefreshToken() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid_refresh_token")
                .build();

        AuthResponse response = AuthResponse.builder()
                .accessToken("new_access_token")
                .refreshToken("new_refresh_token")
                .tokenType("Bearer")
                .build();

        when(authService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new_access_token"));
    }

    @Test
    void testGetCurrentUser() throws Exception {
        User currentUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .role(Role.USER)
                .build();
        currentUser.setId(1L);

        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .build();

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(currentUser, null, currentUser.getAuthorities())
        );

        when(authService.getCurrentUser(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/auth/me").with(user(currentUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("user@techstore.vn"));
    }
}

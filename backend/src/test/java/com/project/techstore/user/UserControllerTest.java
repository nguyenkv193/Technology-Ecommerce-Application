package com.project.techstore.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.user.controller.UserController;
import com.project.techstore.user.dto.*;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.service.AddressService;
import com.project.techstore.user.service.UserService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AddressService addressService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserDetailsService userDetailsService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = User.builder()
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .role(Role.USER)
                .build();
        sampleUser.setId(1L);

        org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(sampleUser, null, sampleUser.getAuthorities())
        );
    }

    @Test
    void testGetProfile() throws Exception {
        UserResponse response = UserResponse.builder()
                .id(1L)
                .email("user@techstore.vn")
                .fullName("Nguyen Van A")
                .build();

        when(userService.getProfile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/users/profile").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value("user@techstore.vn"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        UserProfileUpdateRequest request = UserProfileUpdateRequest.builder()
                .fullName("Nguyen Van B")
                .phone("0909999999")
                .build();

        UserResponse response = UserResponse.builder()
                .id(1L)
                .fullName("Nguyen Van B")
                .phone("0909999999")
                .build();

        when(userService.updateProfile(eq(1L), any(UserProfileUpdateRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/profile")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.fullName").value("Nguyen Van B"));
    }

    @Test
    void testChangePassword() throws Exception {
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("CurrentPass123")
                .newPassword("NewPass123456")
                .build();

        doNothing().when(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));

        mockMvc.perform(post("/api/v1/users/change-password")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(userService).changePassword(eq(1L), any(ChangePasswordRequest.class));
    }

    @Test
    void testGetAddresses() throws Exception {
        AddressResponse addr = AddressResponse.builder()
                .id(10L)
                .recipientName("Nguyen Van A")
                .addressDetail("123 Le Loi")
                .isDefault(true)
                .build();

        when(addressService.getUserAddresses(1L)).thenReturn(List.of(addr));

        mockMvc.perform(get("/api/v1/users/addresses").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].recipientName").value("Nguyen Van A"));
    }

    @Test
    void testCreateAddress() throws Exception {
        AddressRequest request = AddressRequest.builder()
                .recipientName("Nguyen Van A")
                .phone("0901234567")
                .province("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Bến Nghé")
                .addressDetail("123 Lê Lợi")
                .isDefault(true)
                .build();

        AddressResponse response = AddressResponse.builder()
                .id(11L)
                .recipientName("Nguyen Van A")
                .isDefault(true)
                .build();

        when(addressService.createAddress(eq(1L), any(AddressRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/users/addresses")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(11));
    }

    @Test
    void testUpdateAddress() throws Exception {
        AddressRequest request = AddressRequest.builder()
                .recipientName("Nguyen Van C")
                .phone("0901234567")
                .province("Hồ Chí Minh")
                .district("Quận 1")
                .ward("Bến Nghé")
                .addressDetail("456 Lê Lợi")
                .build();

        AddressResponse response = AddressResponse.builder()
                .id(11L)
                .recipientName("Nguyen Van C")
                .build();

        when(addressService.updateAddress(eq(1L), eq(11L), any(AddressRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/users/addresses/11")
                        .with(user(sampleUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.recipientName").value("Nguyen Van C"));
    }

    @Test
    void testDeleteAddress() throws Exception {
        doNothing().when(addressService).deleteAddress(1L, 11L);

        mockMvc.perform(delete("/api/v1/users/addresses/11").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(addressService).deleteAddress(1L, 11L);
    }

    @Test
    void testSetDefaultAddress() throws Exception {
        AddressResponse response = AddressResponse.builder()
                .id(11L)
                .isDefault(true)
                .build();

        when(addressService.setDefaultAddress(1L, 11L)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/users/addresses/11/default").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isDefault").value(true));
    }
}

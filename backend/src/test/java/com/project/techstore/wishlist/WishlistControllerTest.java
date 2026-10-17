package com.project.techstore.wishlist;

import com.project.techstore.auth.security.JwtAuthenticationFilter;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.wishlist.controller.WishlistController;
import com.project.techstore.wishlist.dto.WishlistResponse;
import com.project.techstore.wishlist.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false)
class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

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
    void testGetWishlist() throws Exception {
        WishlistResponse item = WishlistResponse.builder()
                .id(1L)
                .productId(100L)
                .productName("MacBook Pro 16")
                .build();

        when(wishlistService.getUserWishlist(1L)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/wishlist").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].productName").value("MacBook Pro 16"));
    }

    @Test
    void testAddToWishlist() throws Exception {
        WishlistResponse item = WishlistResponse.builder()
                .id(1L)
                .productId(100L)
                .productName("MacBook Pro 16")
                .build();

        when(wishlistService.addToWishlist(1L, 100L)).thenReturn(item);

        mockMvc.perform(post("/api/v1/wishlist/100").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.productId").value(100));
    }

    @Test
    void testRemoveFromWishlist() throws Exception {
        doNothing().when(wishlistService).removeFromWishlist(1L, 100L);

        mockMvc.perform(delete("/api/v1/wishlist/100").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        verify(wishlistService).removeFromWishlist(1L, 100L);
    }

    @Test
    void testCheckWishlist() throws Exception {
        when(wishlistService.isWishlisted(1L, 100L)).thenReturn(true);

        mockMvc.perform(get("/api/v1/wishlist/check/100").with(user(sampleUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.isWishlisted").value(true));
    }
}

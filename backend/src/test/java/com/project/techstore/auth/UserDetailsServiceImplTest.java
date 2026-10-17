package com.project.techstore.auth;

import com.project.techstore.auth.security.UserDetailsServiceImpl;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsernameSuccess() {
        User user = User.builder()
                .email("test@techstore.vn")
                .password("encoded_pass")
                .role(Role.USER)
                .build();

        when(userRepository.findByEmail("test@techstore.vn")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("test@techstore.vn");

        assertNotNull(userDetails);
        assertEquals("test@techstore.vn", userDetails.getUsername());
        assertEquals("encoded_pass", userDetails.getPassword());
        verify(userRepository).findByEmail("test@techstore.vn");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail("notfound@techstore.vn")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                userDetailsService.loadUserByUsername("notfound@techstore.vn")
        );

        verify(userRepository).findByEmail("notfound@techstore.vn");
    }
}

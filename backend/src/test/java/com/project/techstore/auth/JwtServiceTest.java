package com.project.techstore.auth;

import com.project.techstore.auth.security.JwtService;
import com.project.techstore.user.entity.Role;
import com.project.techstore.user.entity.User;
import com.project.techstore.user.entity.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User sampleUser;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // 256-bit base64 test secret key
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "accessTokenExpiration", 1800000L);
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpiration", 2592000000L);

        sampleUser = User.builder()
                .email("student@techstore.project.com")
                .password("encoded_pass")
                .fullName("Student Tester")
                .role(Role.USER)
                .status(UserStatus.ACTIVE)
                .build();
        sampleUser.setId(1001L);
    }

    @Test
    void testGenerateAndValidateAccessToken() {
        String token = jwtService.generateAccessToken(sampleUser);
        assertNotNull(token);
        assertFalse(token.isEmpty());

        assertEquals("student@techstore.project.com", jwtService.extractUsername(token));
        assertEquals(1001L, jwtService.extractUserId(token));
        assertEquals("ACCESS", jwtService.extractTokenType(token));
        assertTrue(jwtService.isTokenValid(token, sampleUser));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void testGenerateRefreshToken() {
        String refreshToken = jwtService.generateRefreshToken(sampleUser);
        assertNotNull(refreshToken);

        assertEquals("student@techstore.project.com", jwtService.extractUsername(refreshToken));
        assertEquals(1001L, jwtService.extractUserId(refreshToken));
        assertEquals("REFRESH", jwtService.extractTokenType(refreshToken));
    }
}

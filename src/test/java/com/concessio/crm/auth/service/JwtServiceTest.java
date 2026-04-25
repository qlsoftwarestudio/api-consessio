package com.concessio.crm.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // Inject required properties using reflection
        ReflectionTestUtils.setField(jwtService, "secret", "test-secret-key-for-jwt-testing-256-bit-long-key-123456789012");
        ReflectionTestUtils.setField(jwtService, "expiration", 86400000L); // 24 hours
    }

    @Test
    void testGenerateToken_Success() {
        String token = jwtService.generateToken("test@example.com", 1L);

        assertNotNull(token);
        assertTrue(token.length() > 50);
    }

    @Test
    void testExtractEmail_Success() {
        String token = jwtService.generateToken("test@example.com", 1L);

        String email = jwtService.extractEmail(token);

        assertEquals("test@example.com", email);
    }

    @Test
    void testExtractTenantId_Success() {
        String token = jwtService.generateToken("test@example.com", 5L);

        Long tenantId = jwtService.extractTenantId(token);

        assertEquals(5L, tenantId);
    }

    @Test
    void testIsTokenValid_ValidToken() {
        String token = jwtService.generateToken("test@example.com", 1L);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username("test@example.com")
                .password("password")
                .roles("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    void testIsTokenValid_InvalidUsername() {
        String token = jwtService.generateToken("test@example.com", 1L);

        UserDetails wrongUser = org.springframework.security.core.userdetails.User.builder()
                .username("wrong@example.com")
                .password("password")
                .roles("USER")
                .build();

        boolean isValid = jwtService.isTokenValid(token, wrongUser);

        assertFalse(isValid);
    }
}

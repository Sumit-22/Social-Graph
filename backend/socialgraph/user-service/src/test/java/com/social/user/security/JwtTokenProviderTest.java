package com.social.user.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

// Use MockitoExtension instead of SpringBootTest for Unit Tests
@ExtendWith(MockitoExtension.class)
public class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        // We inject the properties manually so we don't need the Spring Context
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "this-is-a-very-long-secret-key-for-testing-purposes-that-meets-hs512-requirements-of-64-bytes-at-least");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationInMs", 3600000L);
        ReflectionTestUtils.setField(jwtTokenProvider, "refreshExpirationInMs", 7200000L);
    }

    @Test
    public void testGenerateAndValidateToken() {
        String userId = "user123";
        String username = "testuser";

        String token = jwtTokenProvider.generateToken(userId, username);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(userId, jwtTokenProvider.getUserIdFromToken(token));
        assertEquals(username, jwtTokenProvider.getUsernameFromToken(token));
    }

    @Test
    public void testInvalidToken() {
        String invalidToken = "invalid.token.here";
        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    public void testRefreshToken() {
        String userId = "user123";
        String refreshToken = jwtTokenProvider.generateRefreshToken(userId);

        assertNotNull(refreshToken);
        assertTrue(jwtTokenProvider.validateToken(refreshToken));
    }
}
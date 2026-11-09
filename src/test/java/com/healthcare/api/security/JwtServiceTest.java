package com.healthcare.api.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    // Demo-only test secret (at least 32 characters, as HS256 requires).
    private static final String SECRET =
            "demo-only-secret-key-for-healthcare-api-change-in-production-123456";

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET, 3_600_000L);
    }

    @Test
    void generateToken_thenExtractUsername_returnsUsername() {
        String token = jwtService.generateToken("demo");

        assertEquals("demo", jwtService.extractUsername(token));
    }

    @Test
    void generateToken_thenValidateWithMatchingUser_returnsTrue() {
        String token = jwtService.generateToken("demo");
        var userDetails = User.withUsername("demo").password("demo123").roles("USER").build();

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void tamperedToken_failsValidation() {
        String token = jwtService.generateToken("demo");
        char lastChar = token.charAt(token.length() - 1);
        String tampered = token.substring(0, token.length() - 1)
                + (lastChar == 'a' ? 'b' : 'a');

        assertThrows(JwtException.class, () -> jwtService.extractUsername(tampered));
    }

    @Test
    void tokenForOtherUser_isNotValidForDifferentUser() {
        String token = jwtService.generateToken("demo");
        var otherUser = User.withUsername("someone-else").password("x").roles("USER").build();

        assertThrows(JwtException.class, () -> jwtService.extractUsername(token + "tampered"));
        // A well-formed token for "demo" must not validate as another user.
        org.junit.jupiter.api.Assertions.assertFalse(jwtService.isTokenValid(token, otherUser));
    }
}

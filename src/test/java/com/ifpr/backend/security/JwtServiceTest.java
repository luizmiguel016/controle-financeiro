package com.ifpr.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ifpr.backend.entity.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

class JwtServiceTest {

    private JwtService jwtService;
    private String base64Secret;

    @BeforeEach
    void setUp() {

        base64Secret = Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );

        jwtService = new JwtService(base64Secret, 60000);
    }

    @Test
    void shouldGenerateValidTokenForUser() {

        User user = new User();
        user.setId(1L);
        user.setEmail("luiz@email.com");

        String token = jwtService.generateToken(user);

        Long extractedId = jwtService.extractUserId(token);

        assertEquals(1L, extractedId);
    }

    @Test
    void shouldRejectTokenWithInvalidSignature() {

        User user = new User();
        user.setId(1L);
        user.setEmail("luiz@email.com");

        String token = jwtService.generateToken(user);

        String anotherSecret = Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );

        JwtService anotherJwtService = new JwtService(
                anotherSecret,
                60000
        );

        assertThrows(
                JwtException.class,
                () -> anotherJwtService.extractUserId(token)
        );
    }

    @Test
    void shouldRejectExpiredToken() {

        JwtService expiredJwtService = new JwtService(
                base64Secret,
                -1000
        );

        User user = new User();
        user.setId(1L);
        user.setEmail("luiz@email.com");

        String token = expiredJwtService.generateToken(user);

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.extractUserId(token)
        );
    }
}
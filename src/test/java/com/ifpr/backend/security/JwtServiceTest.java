package com.ifpr.backend.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ifpr.backend.entity.Usuario;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;

class JwtServiceTest {

    private JwtService jwtService;
    private String segredoBase64;

    @BeforeEach
    void preparar() {

        segredoBase64 = Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );

        jwtService = new JwtService(segredoBase64, 60000);
    }

    @Test
    void deveGerarTokenValidoParaUsuario() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("luiz@email.com");

        String token = jwtService.gerarToken(usuario);

        Long idExtraido = jwtService.extrairIdUsuario(token);

        assertEquals(1L, idExtraido);
    }

    @Test
    void deveRejeitarTokenComAssinaturaInvalida() {

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("luiz@email.com");

        String token = jwtService.gerarToken(usuario);

        String outraChave = Encoders.BASE64.encode(
                Jwts.SIG.HS256.key().build().getEncoded()
        );

        JwtService outroJwtService = new JwtService(outraChave, 60000);

        assertThrows(
                JwtException.class,
                () -> outroJwtService.extrairIdUsuario(token)
        );
    }

    @Test
    void deveRejeitarTokenExpirado() {

        JwtService servicoExpirado = new JwtService(
                segredoBase64,
                -1000
        );

        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setEmail("luiz@email.com");

        String token = servicoExpirado.gerarToken(usuario);

        assertThrows(
                ExpiredJwtException.class,
                () -> jwtService.extrairIdUsuario(token)
        );
    }
}
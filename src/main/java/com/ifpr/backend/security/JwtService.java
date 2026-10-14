package com.ifpr.backend.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ifpr.backend.entity.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey chave;
    private final long expiracao;

    public JwtService(@Value("${jwt.secret}") String segredoBase64, @Value("${jwt.expiration}") long expiracao) {

        this.chave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(segredoBase64));

        this.expiracao = expiracao;
    }

    public String gerarToken(Usuario usuario) {

        Date agora = new Date();
        Date expiraEm = new Date(agora.getTime() + expiracao);

        return Jwts.builder()
                .subject(usuario.getId().toString())
                .claim("email", usuario.getEmail())
                .issuedAt(agora)
                .expiration(expiraEm)
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    public Long extrairIdUsuario(String token) {

        String id = Jwts.parser()
                .verifyWith(chave)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        return Long.valueOf(id);
    }
}
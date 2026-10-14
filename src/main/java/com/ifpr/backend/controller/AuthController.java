package com.ifpr.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifpr.backend.dto.TokenRespostaDTO;
import com.ifpr.backend.dto.UsuarioLoginDTO;
import com.ifpr.backend.dto.UsuarioRespostaDTO;
import com.ifpr.backend.entity.Usuario;
import com.ifpr.backend.service.AutenticacaoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticacaoService autenticacaoService;
    

    public AuthController(AutenticacaoService autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenRespostaDTO> login(@Valid @RequestBody UsuarioLoginDTO dto) {

        TokenRespostaDTO resposta = autenticacaoService.login(dto);

        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioRespostaDTO> consultarUsuario(@AuthenticationPrincipal Usuario usuario) {

        UsuarioRespostaDTO resposta = new UsuarioRespostaDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCriadoEm()
        );

        return ResponseEntity.ok(resposta);
    }
}
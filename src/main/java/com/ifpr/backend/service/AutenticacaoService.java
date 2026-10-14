package com.ifpr.backend.service;

import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ifpr.backend.dto.TokenRespostaDTO;
import com.ifpr.backend.dto.UsuarioLoginDTO;
import com.ifpr.backend.entity.Usuario;
import com.ifpr.backend.repository.UsuarioRepository;
import com.ifpr.backend.security.JwtService;


@Service 
public class AutenticacaoService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AutenticacaoService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {
        
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public Usuario autenticar(UsuarioLoginDTO dto) {
        
        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException("E-mail ou senha inválidos")
            );

        if (!passwordEncoder.matches(
                dto.senha(),
                usuario.getSenhaCriptografada())) {

            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        return usuario;
    }

    public TokenRespostaDTO login(UsuarioLoginDTO dto) {
        
        Usuario usuario = autenticar(dto);

        String token = jwtService.gerarToken(usuario);

        return new TokenRespostaDTO(token, "Bearer");
    }
}

package com.ifpr.backend.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ifpr.backend.dto.UsuarioCadastroDTO;
import com.ifpr.backend.dto.UsuarioRespostaDTO;
import com.ifpr.backend.entity.Usuario;
import com.ifpr.backend.repository.UsuarioRepository;

@Service 
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UsuarioRespostaDTO cadastrar(UsuarioCadastroDTO dto) {

        String email = dto.email().trim().toLowerCase();

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new RuntimeException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome().trim());
        usuario.setEmail(email);
        usuario.setSenhaCriptografada(passwordEncoder.encode(dto.senha()));
        usuario.setCriadoEm(LocalDateTime.now());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return new UsuarioRespostaDTO(
                usuarioSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getCriadoEm()
        );
        
    }
}

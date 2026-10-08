package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.ifpr.backend.dto.UsuarioLoginDTO;
import com.ifpr.backend.entity.Usuario;
import com.ifpr.backend.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
public class AutenticacaoServiceTest {
    
    @Mock
    private UsuarioRepository usuarioRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private AutenticacaoService autenticacaoService;

    @BeforeEach 
    void preparar() {
        autenticacaoService = new AutenticacaoService(
                usuarioRepository,
                passwordEncoder   
        );
    }

    @Test
    void deveAutenticarComCredenciaisValidas() {
        
        Usuario usuario = new Usuario();
        usuario.setEmail("luiz@email.com");
        usuario.setSenhaCriptografada(passwordEncoder.encode("Senha@123"));

        when(usuarioRepository.findByEmailIgnoreCase("luiz@email.com")).thenReturn(Optional.of(usuario));

        UsuarioLoginDTO dto = new UsuarioLoginDTO("  LUIZ@EMAIL.COM  ", "Senha@123");

        Usuario resultado = autenticacaoService.autenticar(dto);

        assertSame(usuario, resultado);
    }

    @Test
    void deveRejeitarEmailInexistente() {

        when(usuarioRepository.findByEmailIgnoreCase("inexistente@email.com")).thenReturn(Optional.empty());

        UsuarioLoginDTO dto = new UsuarioLoginDTO("inexistente@email.com", "Senha@123");

        assertThrows(BadCredentialsException.class, () -> autenticacaoService.autenticar(dto));
    }

    @Test
    void deveRejeitarSenhaIncorreta() {

        Usuario usuario = new Usuario();
        usuario.setEmail("luiz@email.com");
        usuario.setSenhaCriptografada(passwordEncoder.encode("SenhaCorreta123"));

        when(usuarioRepository.findByEmailIgnoreCase("luiz@email.com")).thenReturn(Optional.of(usuario));

        UsuarioLoginDTO dto = new UsuarioLoginDTO("luiz@email.com", "SenhaErrada123");

        assertThrows(BadCredentialsException.class, () -> autenticacaoService.autenticar(dto));
    }
}

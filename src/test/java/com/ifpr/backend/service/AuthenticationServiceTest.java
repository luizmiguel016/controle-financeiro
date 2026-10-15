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

import com.ifpr.backend.dto.UserLoginDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.repository.UserRepository;
import com.ifpr.backend.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {

        authenticationService = new AuthenticationService(
                userRepository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void shouldAuthenticateWithValidCredentials() {

        User user = new User();
        user.setEmail("luiz@email.com");
        user.setPasswordHash(
                passwordEncoder.encode("Senha@123")
        );

        when(userRepository.findByEmailIgnoreCase("luiz@email.com"))
                .thenReturn(Optional.of(user));

        UserLoginDTO dto = new UserLoginDTO(
                "  LUIZ@EMAIL.COM  ",
                "Senha@123"
        );

        User result = authenticationService.authenticate(dto);

        assertSame(user, result);
    }

    @Test
    void shouldRejectUnknownEmail() {

        when(userRepository.findByEmailIgnoreCase("inexistente@email.com"))
                .thenReturn(Optional.empty());

        UserLoginDTO dto = new UserLoginDTO(
                "inexistente@email.com",
                "Senha@123"
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(dto)
        );
    }

    @Test
    void shouldRejectIncorrectPassword() {

        User user = new User();
        user.setEmail("luiz@email.com");
        user.setPasswordHash(
                passwordEncoder.encode("SenhaCorreta123")
        );

        when(userRepository.findByEmailIgnoreCase("luiz@email.com"))
                .thenReturn(Optional.of(user));

        UserLoginDTO dto = new UserLoginDTO(
                "luiz@email.com",
                "SenhaErrada123"
        );

        assertThrows(
                BadCredentialsException.class,
                () -> authenticationService.authenticate(dto)
        );
    }
}
package com.ifpr.backend.service;

import java.util.Locale;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ifpr.backend.dto.TokenResponseDTO;
import com.ifpr.backend.dto.UserLoginDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.repository.UserRepository;
import com.ifpr.backend.security.JwtService;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User authenticate(UserLoginDTO dto) {

        String email = dto.email().trim().toLowerCase(Locale.ROOT);

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new BadCredentialsException("E-mail ou senha inválidos")
                );

        if (!passwordEncoder.matches(
                dto.password(),
                user.getPasswordHash())) {

            throw new BadCredentialsException("E-mail ou senha inválidos");
        }

        return user;
    }

    public TokenResponseDTO login(UserLoginDTO dto) {

        User user = authenticate(dto);

        String token = jwtService.generateToken(user);

        return new TokenResponseDTO(token, "Bearer");
    }
}
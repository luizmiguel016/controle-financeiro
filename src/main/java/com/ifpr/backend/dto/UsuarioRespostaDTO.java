package com.ifpr.backend.dto;

import java.time.LocalDateTime;

public record UsuarioRespostaDTO(
        Long id,
        String nome,
        String email,
        LocalDateTime criadoEm
) {
}

package com.ifpr.backend.exception;

import java.time.LocalDateTime;

public record ErroResposta(
        int status,
        String mensagem,
        LocalDateTime timestamp
) {
}
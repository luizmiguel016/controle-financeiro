package com.ifpr.backend.exception;

import java.time.LocalDateTime;

public record ErroResposta(
        LocalDateTime timestap,
        int status,
        String error,
        String message
) {
}
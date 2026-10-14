package com.ifpr.backend.dto;

public record TokenRespostaDTO(
        String accessToken,
        String tokenType
) {
}
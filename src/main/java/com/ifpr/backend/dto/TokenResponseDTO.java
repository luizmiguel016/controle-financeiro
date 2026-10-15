package com.ifpr.backend.dto;

public record TokenResponseDTO(
        String accessToken,
        String tokenType
) {
}
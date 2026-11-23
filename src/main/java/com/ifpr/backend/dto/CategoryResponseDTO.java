package com.ifpr.backend.dto;

import com.ifpr.backend.entity.TransactionType;

public record CategoryResponseDTO(
        Long id,
        String name,
        TransactionType type,
        Long userId
) {
}
package com.ifpr.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ifpr.backend.entity.TransactionType;

public record TransactionResponseDTO(
        Long id,
        String description,
        BigDecimal amount,
        TransactionType type,
        LocalDate transactionDate,
        Long walletId,
        Long categoryId,
        Long createdById,
        LocalDateTime createdAt
) {
}

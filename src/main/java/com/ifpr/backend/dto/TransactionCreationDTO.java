package com.ifpr.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ifpr.backend.entity.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TransactionCreationDTO(

        @NotBlank(message = "A descrição da transação é obrigatória")
        @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres")
        String description,

        @NotNull(message = "O valor da transação é obrigatório")
        @DecimalMin(value = "0.01", message = "O valor deve ser de no mínimo R$ 0,01")
        @Digits(integer = 13, fraction = 2, message = "O valor deve ter no máximo 13 dígitos inteiros e 2 decimais")
        BigDecimal amount,

        @NotNull(message = "O tipo da transação é obrigatório")
        TransactionType type,

        @NotNull(message = "A data da transação é obrigatória")
        LocalDate transactionDate,

        @NotNull(message = "A carteira é obrigatória")
        @Positive(message = "O ID da carteira deve ser positivo")
        Long walletId,

        @NotNull(message = "A categoria é obrigatória")
        @Positive(message = "O ID da categoria deve ser positivo")
        Long categoryId

) {
}

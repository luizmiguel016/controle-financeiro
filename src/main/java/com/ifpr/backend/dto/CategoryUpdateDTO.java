package com.ifpr.backend.dto;

import com.ifpr.backend.entity.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CategoryUpdateDTO(

    @NotBlank(message = "O nome da categoria é obriatório")
    @Size(max = 80, message = "O nome da categoria deve ter no máximo 80 caracteres")
    String name,

    @NotNull(message = "O tipo da categoria é obrigatório")
    TransactionType type
)
{}
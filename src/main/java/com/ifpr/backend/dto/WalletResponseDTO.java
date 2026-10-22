package com.ifpr.backend.dto;

import java.time.LocalDateTime;

public record WalletResponseDTO(
    Long id,
    String name,
    Long ownerId,
    LocalDateTime createdAt
) {
    
}

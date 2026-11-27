
package com.ifpr.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ifpr.backend.dto.TransactionCreationDTO;
import com.ifpr.backend.dto.TransactionResponseDTO;
import com.ifpr.backend.entity.TransactionType;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.TransactionService;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Test
    void shouldCreateTransactionAndReturn201() {

        User author = new User();
        author.setId(3L);

        LocalDate transactionDate =
                LocalDate.of(2026, 11, 26);

        LocalDateTime createdAt =
                LocalDateTime.of(2026, 11, 26, 20, 30);

        TransactionCreationDTO request =
                new TransactionCreationDTO(
                        "Compra no supermercado",
                        new BigDecimal("189.90"),
                        TransactionType.EXPENSE,
                        transactionDate,
                        10L,
                        20L
                );

        TransactionResponseDTO expectedResponse =
                new TransactionResponseDTO(
                        100L,
                        "Compra no supermercado",
                        new BigDecimal("189.90"),
                        TransactionType.EXPENSE,
                        transactionDate,
                        10L,
                        20L,
                        3L,
                        createdAt
                );

        when(transactionService.create(request, author))
                .thenReturn(Optional.of(expectedResponse));

        ResponseEntity<TransactionResponseDTO> result =
                transactionController.create(request, author);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(expectedResponse, result.getBody());

        verify(transactionService).create(request, author);
    }

    @Test
    void shouldReturn404WhenTransactionResourcesAreNotAccessible() {

        User author = new User();
        author.setId(4L);

        TransactionCreationDTO request =
                new TransactionCreationDTO(
                        "Compra não autorizada",
                        new BigDecimal("50.00"),
                        TransactionType.EXPENSE,
                        LocalDate.of(2026, 11, 26),
                        10L,
                        20L
                );

        when(transactionService.create(request, author))
                .thenReturn(Optional.empty());

        ResponseEntity<TransactionResponseDTO> result =
                transactionController.create(request, author);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(transactionService).create(request, author);
    }
}

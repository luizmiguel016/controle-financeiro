package com.ifpr.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.WalletService;

@ExtendWith(MockitoExtension.class)
class WalletLookupControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void shouldReturn200WhenWalletExists() {

        User owner = new User();
        owner.setId(3L);

        WalletResponseDTO wallet = new WalletResponseDTO(
                1L,
                "Despesas da Casa",
                3L,
                LocalDateTime.now()
        );

        when(walletService.findOwnedById(1L, owner))
                .thenReturn(Optional.of(wallet));

        ResponseEntity<WalletResponseDTO> response =
                walletController.findById(1L, owner);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(wallet, response.getBody());
    }

    @Test
    void shouldReturn404WhenWalletIsNotAccessible() {

        User owner = new User();
        owner.setId(4L);

        when(walletService.findOwnedById(1L, owner))
                .thenReturn(Optional.empty());

        ResponseEntity<WalletResponseDTO> response =
                walletController.findById(1L, owner);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
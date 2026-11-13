package com.ifpr.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ifpr.backend.dto.WalletCreationDTO;
import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.WalletService;

@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @InjectMocks
    private WalletController walletController;

    @Test
    void shouldCreateWalletAndReturn201() {

        User owner = new User();
        owner.setId(3L);

        WalletCreationDTO request =
                new WalletCreationDTO("Despesas da Casa");

        WalletResponseDTO expectedResponse = new WalletResponseDTO(
                1L,
                "Despesas da Casa",
                3L,
                LocalDateTime.now()
        );

        when(walletService.create(request, owner))
                .thenReturn(expectedResponse);

        ResponseEntity<WalletResponseDTO> result =
                walletController.create(request, owner);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(expectedResponse, result.getBody());

        verify(walletService).create(request, owner);
    }

    @Test
    void shouldListOwnedWalletsAndReturn200() {

        User owner = new User();
        owner.setId(3L);

        WalletResponseDTO wallet = new WalletResponseDTO(
                1L,
                "Despesas da Casa",
                3L,
                LocalDateTime.now()
        );

        List<WalletResponseDTO> expectedResponse = List.of(wallet);

        when(walletService.listOwned(owner))
                .thenReturn(expectedResponse);

        ResponseEntity<List<WalletResponseDTO>> result =
                walletController.list(owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());

        verify(walletService).listOwned(owner);
    }

    @Test
    void shouldFindOwnedWalletAndReturn200() {

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

        ResponseEntity<WalletResponseDTO> result =
                walletController.findById(1L, owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(wallet, result.getBody());

        verify(walletService).findOwnedById(1L, owner);
    }

    @Test
    void shouldReturn404WhenWalletIsNotAccessible() {

        User owner = new User();
        owner.setId(4L);

        when(walletService.findOwnedById(1L, owner))
                .thenReturn(Optional.empty());

        ResponseEntity<WalletResponseDTO> result =
                walletController.findById(1L, owner);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(walletService).findOwnedById(1L, owner);
    }
}
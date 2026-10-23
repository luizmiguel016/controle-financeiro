package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletLookupServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(walletRepository);
    }

    @Test
    void shouldFindWalletOwnedByAuthenticatedUser() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("Despesas da Casa");
        wallet.setOwner(owner);
        wallet.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findByIdAndOwner_Id(1L, 3L))
                .thenReturn(Optional.of(wallet));

        Optional<WalletResponseDTO> response =
                walletService.findOwnedById(1L, owner);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().id());
        assertEquals("Despesas da Casa", response.get().name());
        assertEquals(3L, response.get().ownerId());

        verify(walletRepository).findByIdAndOwner_Id(1L, 3L);
    }

    @Test
    void shouldReturnEmptyWhenWalletIsNotOwnedByUser() {

        User owner = new User();
        owner.setId(4L);

        when(walletRepository.findByIdAndOwner_Id(1L, 4L))
                .thenReturn(Optional.empty());

        Optional<WalletResponseDTO> response =
                walletService.findOwnedById(1L, owner);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(1L, 4L);
    }
}
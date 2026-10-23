package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletListingServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Test
    void shouldListOnlyWalletsOwnedByAuthenticatedUser() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("Despesas da Casa");
        wallet.setOwner(owner);
        wallet.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findByOwner_Id(3L))
                .thenReturn(List.of(wallet));

        WalletService walletService = new WalletService(walletRepository);

        List<WalletResponseDTO> response =
                walletService.listOwned(owner);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("Despesas da Casa", response.get(0).name());
        assertEquals(3L, response.get(0).ownerId());

        verify(walletRepository).findByOwner_Id(3L);
    }
}
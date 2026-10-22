package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.WalletCreationDTO;
import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.repository.WalletRepository;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    private WalletService walletService;

    @BeforeEach
    void setUp() {
        walletService = new WalletService(walletRepository);
    }

    @Test
    void shouldCreateWalletForAuthenticatedUser() {

        User owner = new User();
        owner.setId(10L);

        WalletCreationDTO dto = new WalletCreationDTO("  Despesas da Casa  ");

        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> {

            Wallet wallet = invocation.getArgument(0);
            wallet.setId(1L);

            return wallet;
        });

        WalletResponseDTO response = walletService.create(dto, owner);

        assertEquals(1L, response.id());
        assertEquals("Despesas da Casa", response.name());
        assertEquals(10L, response.ownerId());
        assertNotNull(response.createdAt());

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);

        verify(walletRepository).save(walletCaptor.capture());

        Wallet savedWallet = walletCaptor.getValue();

        assertSame(owner, savedWallet.getOwner());
    }
}
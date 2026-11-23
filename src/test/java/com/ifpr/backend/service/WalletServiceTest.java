
package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.WalletCreationDTO;
import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.dto.WalletUpdateDTO;
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
        owner.setId(3L);

        WalletCreationDTO dto =
                new WalletCreationDTO("  Despesas da Casa  ");

        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(invocation -> {
                    Wallet wallet = invocation.getArgument(0);
                    wallet.setId(1L);
                    return wallet;
                });

        WalletResponseDTO response =
                walletService.create(dto, owner);

        assertEquals(1L, response.id());
        assertEquals("Despesas da Casa", response.name());
        assertEquals(3L, response.ownerId());
        assertNotNull(response.createdAt());

        ArgumentCaptor<Wallet> walletCaptor =
                ArgumentCaptor.forClass(Wallet.class);

        verify(walletRepository).save(walletCaptor.capture());

        assertSame(owner, walletCaptor.getValue().getOwner());
    }

    @Test
    void shouldListOnlyOwnedWallets() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("Despesas da Casa");
        wallet.setOwner(owner);
        wallet.setCreatedAt(LocalDateTime.now());

        when(walletRepository.findByOwner_Id(3L))
                .thenReturn(List.of(wallet));

        List<WalletResponseDTO> response =
                walletService.listOwned(owner);

        assertEquals(1, response.size());
        assertEquals(1L, response.get(0).id());
        assertEquals("Despesas da Casa", response.get(0).name());
        assertEquals(3L, response.get(0).ownerId());

        verify(walletRepository).findByOwner_Id(3L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoWallets() {

        User owner = new User();
        owner.setId(4L);

        when(walletRepository.findByOwner_Id(4L))
                .thenReturn(List.of());

        List<WalletResponseDTO> response =
                walletService.listOwned(owner);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByOwner_Id(4L);
    }

    @Test
    void shouldFindWalletByIdAndOwner() {

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
        assertEquals(3L, response.get().ownerId());

        verify(walletRepository).findByIdAndOwner_Id(1L, 3L);
    }

    @Test
    void shouldNotFindWalletOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        when(walletRepository.findByIdAndOwner_Id(1L, 4L))
                .thenReturn(Optional.empty());

        Optional<WalletResponseDTO> response =
                walletService.findOwnedById(1L, owner);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(1L, 4L);
    }

    @Test
    void shouldUpdateWalletOwnedByUser() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setName("Despesas da Casa");
        wallet.setOwner(owner);
        wallet.setCreatedAt(LocalDateTime.now());

        WalletUpdateDTO dto =
                new WalletUpdateDTO("  Despesas Familiares  ");

        when(walletRepository.findByIdAndOwner_Id(1L, 3L))
                .thenReturn(Optional.of(wallet));

        when(walletRepository.save(wallet))
                .thenReturn(wallet);

        Optional<WalletResponseDTO> response =
                walletService.updateOwned(1L, dto, owner);

        assertTrue(response.isPresent());
        assertEquals("Despesas Familiares", response.get().name());
        assertEquals(3L, response.get().ownerId());
        assertEquals("Despesas Familiares", wallet.getName());

        verify(walletRepository).findByIdAndOwner_Id(1L, 3L);
        verify(walletRepository).save(wallet);
    }

    @Test
    void shouldNotUpdateWalletOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        WalletUpdateDTO dto =
                new WalletUpdateDTO("Nome Alterado");

        when(walletRepository.findByIdAndOwner_Id(1L, 4L))
                .thenReturn(Optional.empty());

        Optional<WalletResponseDTO> response =
                walletService.updateOwned(1L, dto, owner);

        assertTrue(response.isEmpty());

        verify(walletRepository).findByIdAndOwner_Id(1L, 4L);
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void shouldDeleteWalletOwnedByUser() {

        User owner = new User();
        owner.setId(3L);

        Wallet wallet = new Wallet();
        wallet.setId(1L);
        wallet.setOwner(owner);

        when(walletRepository.findByIdAndOwner_Id(1L, 3L))
                .thenReturn(Optional.of(wallet));

        boolean deleted = walletService.deleteOwned(1L, owner);

        assertTrue(deleted);

        verify(walletRepository).findByIdAndOwner_Id(1L, 3L);
        verify(walletRepository).delete(wallet);
    }

    @Test
    void shouldNotDeleteWalletOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        when(walletRepository.findByIdAndOwner_Id(1L, 4L))
                .thenReturn(Optional.empty());

        boolean deleted = walletService.deleteOwned(1L, owner);

        assertFalse(deleted);

        verify(walletRepository).findByIdAndOwner_Id(1L, 4L);
        verify(walletRepository, never()).delete(any(Wallet.class));
    }
}

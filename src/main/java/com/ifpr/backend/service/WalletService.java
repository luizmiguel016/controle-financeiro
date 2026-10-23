package com.ifpr.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpr.backend.dto.WalletCreationDTO;
import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.repository.WalletRepository;

@Service 
public class WalletService {
    
    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponseDTO create(WalletCreationDTO dto, User owner) {

        Wallet wallet = new Wallet();

        wallet.setName(dto.name().trim());
        wallet.setOwner(owner);
        wallet.setCreatedAt(LocalDateTime.now());

        Wallet savedWallet = walletRepository.save(wallet);

        return new WalletResponseDTO(
                savedWallet.getId(),
                savedWallet.getName(),
                owner.getId(),
                savedWallet.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public List<WalletResponseDTO> listOwned(User owner) {

        List<Wallet> wallets = walletRepository.findByOwner_Id(owner.getId());

        return wallets.stream().map(wallet -> new WalletResponseDTO(wallet.getId(), wallet.getName(), owner.getId(), wallet.getCreatedAt())).toList();
    }

    @Transactional(readOnly = true)
    public Optional<WalletResponseDTO> findOwnedById(Long id, User owner) {

        return walletRepository.findByIdAndOwner_Id(id, owner.getId()).map(wallet -> new WalletResponseDTO(
            wallet.getId(),
            wallet.getName(),
            owner.getId(),
            wallet.getCreatedAt()
        ));
    }
}

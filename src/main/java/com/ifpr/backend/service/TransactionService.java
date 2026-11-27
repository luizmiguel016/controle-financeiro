package com.ifpr.backend.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ifpr.backend.dto.TransactionCreationDTO;
import com.ifpr.backend.dto.TransactionResponseDTO;
import com.ifpr.backend.entity.Category;
import com.ifpr.backend.entity.Transaction;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.entity.Wallet;
import com.ifpr.backend.exception.BusinessException;
import com.ifpr.backend.repository.CategoryRepository;
import com.ifpr.backend.repository.TransactionRepository;
import com.ifpr.backend.repository.WalletRepository;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final CategoryRepository categoryRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            WalletRepository walletRepository,
            CategoryRepository categoryRepository) {

        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Optional<TransactionResponseDTO> create(
            TransactionCreationDTO dto,
            User author) {

        Optional<Wallet> wallet =
                walletRepository.findByIdAndOwner_Id(
                        dto.walletId(),
                        author.getId()
                );

        if (wallet.isEmpty()) {
            return Optional.empty();
        }

        Optional<Category> category =
                categoryRepository.findByIdAndUser_Id(dto.categoryId(), author.getId());

        if (category.isEmpty()) {
            return Optional.empty();
        }

        if (category.get().getType() != dto.type()) {
            throw new BusinessException("O tipo da transação deve corresponder ao tipo da categoria");
        }

        Transaction transaction = new Transaction();

        transaction.setDescription(dto.description().trim());
        transaction.setAmount(dto.amount());
        transaction.setType(dto.type());
        transaction.setTransactionDate(dto.transactionDate());
        transaction.setWallet(wallet.get());
        transaction.setCategory(category.get());
        transaction.setCreatedBy(author);
        transaction.setCreatedAt(LocalDateTime.now());

        Transaction savedTransaction = transactionRepository.save(transaction);

        return Optional.of(new TransactionResponseDTO(
                savedTransaction.getId(),
                savedTransaction.getDescription(),
                savedTransaction.getAmount(),
                savedTransaction.getType(),
                savedTransaction.getTransactionDate(),
                savedTransaction.getWallet().getId(),
                savedTransaction.getCategory().getId(),
                savedTransaction.getCreatedBy().getId(),
                savedTransaction.getCreatedAt()
        ));
    }

    @Transactional(readOnly = true)
    public Optional<List<TransactionResponseDTO>> listOwnedByWallet(Long walletId, User owner) {

        Optional<Wallet> wallet =
                walletRepository.findByIdAndOwner_Id(
                        walletId,
                        owner.getId()
                );

        if (wallet.isEmpty()) {
            return Optional.empty();
        }

        List<Transaction> transactions = transactionRepository.findByWallet_Id(walletId);

        List<TransactionResponseDTO> response = transactions.stream()
                .map(transaction -> new TransactionResponseDTO(
                        transaction.getId(),
                        transaction.getDescription(),
                        transaction.getAmount(),
                        transaction.getType(),
                        transaction.getTransactionDate(),
                        transaction.getWallet().getId(),
                        transaction.getCategory().getId(),
                        transaction.getCreatedBy().getId(),
                        transaction.getCreatedAt()
                ))
                .toList();

        return Optional.of(response);
    }
}

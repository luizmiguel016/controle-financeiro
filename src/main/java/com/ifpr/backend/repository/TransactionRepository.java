package com.ifpr.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByWallet_Id(Long walletId);

    Optional<Transaction> findByIdAndWallet_Owner_Id(Long id, Long ownerId);
}

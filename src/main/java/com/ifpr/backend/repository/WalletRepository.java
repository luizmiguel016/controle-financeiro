package com.ifpr.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    List<Wallet> findByOwner_Id(Long ownerId);
    
    Optional<Wallet> findByIdAndOwner_Id(Long id, Long ownerId);
}
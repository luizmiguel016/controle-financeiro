package com.ifpr.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Wallet;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
}

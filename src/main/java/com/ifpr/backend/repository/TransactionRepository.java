package com.ifpr.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ifpr.backend.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
}

package com.ifpr.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ifpr.backend.dto.TransactionCreationDTO;
import com.ifpr.backend.dto.TransactionResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> create (
            @Valid @RequestBody TransactionCreationDTO request,
            @AuthenticationPrincipal User author) {

        Optional<TransactionResponseDTO> response = transactionService.create(request, author);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response.get());
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> list (
            @RequestParam Long walletId,
            @AuthenticationPrincipal User owner) {

        Optional<List<TransactionResponseDTO>> response = transactionService.listOwnedByWallet(walletId, owner);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> findById(@PathVariable Long id, @AuthenticationPrincipal User owner) {

        Optional<TransactionResponseDTO> response = transactionService.findOwnedById(id, owner);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }
    
}

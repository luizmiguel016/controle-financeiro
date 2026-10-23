package com.ifpr.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifpr.backend.dto.WalletCreationDTO;
import com.ifpr.backend.dto.WalletResponseDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.WalletService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponseDTO> create(
            @Valid @RequestBody WalletCreationDTO request,
            @AuthenticationPrincipal User owner) {

        WalletResponseDTO response = walletService.create(request, owner);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<WalletResponseDTO>> list(@AuthenticationPrincipal User owner) {

        List<WalletResponseDTO> response = walletService.listOwned(owner);

        return ResponseEntity.ok(response);
    }
}
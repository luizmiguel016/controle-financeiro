package com.ifpr.backend.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.dto.CategoryUpdateDTO;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryCreationDTO request, @AuthenticationPrincipal User owner) {

        CategoryResponseDTO response = categoryService.create(request, owner);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> list(@AuthenticationPrincipal User owner) {

        List<CategoryResponseDTO> response = categoryService.listOwned(owner);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> findById(@PathVariable Long id, @AuthenticationPrincipal User owner) {

        Optional<CategoryResponseDTO> response = categoryService.findOwnedById(id, owner);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> update (@PathVariable Long id, @Valid @RequestBody CategoryUpdateDTO request, @AuthenticationPrincipal User owner) {
        
        Optional<CategoryResponseDTO> response = categoryService.updateOwned(id, request, owner);

        if (response.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response.get());
    }
}

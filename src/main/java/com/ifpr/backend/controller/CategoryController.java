
package com.ifpr.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
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
}


package com.ifpr.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.entity.TransactionType;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.service.CategoryService;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    @Test
    void shouldCreateCategoryAndReturn201() {

        User owner = new User();
        owner.setId(3L);

        CategoryCreationDTO request = new CategoryCreationDTO(
                "Alimentação",
                TransactionType.EXPENSE
        );

        CategoryResponseDTO expectedResponse =
                new CategoryResponseDTO(
                        1L,
                        "Alimentação",
                        TransactionType.EXPENSE,
                        3L
                );

        when(categoryService.create(request, owner))
                .thenReturn(expectedResponse);

        ResponseEntity<CategoryResponseDTO> result =
                categoryController.create(request, owner);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertSame(expectedResponse, result.getBody());

        verify(categoryService).create(request, owner);
    }
}


package com.ifpr.backend.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.dto.CategoryUpdateDTO;
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

    @Test
    void shouldListOwnedCategoriesAndReturn200() {

        User owner = new User();
        owner.setId(3L);

        CategoryResponseDTO food = new CategoryResponseDTO(
                1L,
                "Alimentação",
                TransactionType.EXPENSE,
                3L
        );

        CategoryResponseDTO salary = new CategoryResponseDTO(
                2L,
                "Salário",
                TransactionType.INCOME,
                3L
        );

        List<CategoryResponseDTO> expectedResponse =
                List.of(food, salary);

        when(categoryService.listOwned(owner))
                .thenReturn(expectedResponse);

        ResponseEntity<List<CategoryResponseDTO>> result =
                categoryController.list(owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(expectedResponse, result.getBody());

        verify(categoryService).listOwned(owner);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoCategories() {

        User owner = new User();
        owner.setId(4L);

        when(categoryService.listOwned(owner))
                .thenReturn(List.of());

        ResponseEntity<List<CategoryResponseDTO>> result =
                categoryController.list(owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertTrue(result.getBody().isEmpty());

        verify(categoryService).listOwned(owner);
    }

    @Test
    void shouldFindOwnedCategoryAndReturn200() {

        User owner = new User();
        owner.setId(3L);

        CategoryResponseDTO expectedResponse =
                new CategoryResponseDTO(
                        1L,
                        "Alimentação",
                        TransactionType.EXPENSE,
                        3L
                );

        when(categoryService.findOwnedById(1L, owner))
                .thenReturn(Optional.of(expectedResponse));

        ResponseEntity<CategoryResponseDTO> result =
                categoryController.findById(1L, owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(expectedResponse, result.getBody());

        verify(categoryService).findOwnedById(1L, owner);
    }

    @Test
    void shouldReturn404WhenCategoryIsNotAccessible() {

        User owner = new User();
        owner.setId(4L);

        when(categoryService.findOwnedById(1L, owner))
                .thenReturn(Optional.empty());

        ResponseEntity<CategoryResponseDTO> result =
                categoryController.findById(1L, owner);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(categoryService).findOwnedById(1L, owner);
    }

    @Test
    void shouldUpdateCategoryAndReturn200() {

        User owner = new User();
        owner.setId(3L);

        CategoryUpdateDTO request = new CategoryUpdateDTO(
                "Supermercado",
                TransactionType.EXPENSE
        );

        CategoryResponseDTO expectedResponse =
                new CategoryResponseDTO(
                        1L,
                        "Supermercado",
                        TransactionType.EXPENSE,
                        3L
                );

        when(categoryService.updateOwned(1L, request, owner))
                .thenReturn(Optional.of(expectedResponse));

        ResponseEntity<CategoryResponseDTO> result =
                categoryController.update(1L, request, owner);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertSame(expectedResponse, result.getBody());

        verify(categoryService).updateOwned(1L, request, owner);
    }

    @Test
    void shouldReturn404WhenUpdatingInaccessibleCategory() {

        User owner = new User();
        owner.setId(4L);

        CategoryUpdateDTO request = new CategoryUpdateDTO(
                "Nome Alterado",
                TransactionType.INCOME
        );

        when(categoryService.updateOwned(1L, request, owner))
                .thenReturn(Optional.empty());

        ResponseEntity<CategoryResponseDTO> result =
                categoryController.update(1L, request, owner);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertNull(result.getBody());

        verify(categoryService).updateOwned(1L, request, owner);
    }
}

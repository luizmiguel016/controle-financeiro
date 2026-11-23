package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.entity.Category;
import com.ifpr.backend.entity.TransactionType;
import com.ifpr.backend.entity.User;
import com.ifpr.backend.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryService(categoryRepository);
    }

    @Test
    void shouldCreateExpenseCategoryForAuthenticatedUser() {

        User owner = new User();
        owner.setId(3L);

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "  Alimentação  ",
                TransactionType.EXPENSE
        );

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {

                    Category category = invocation.getArgument(0);
                    category.setId(1L);

                    return category;
                });

        CategoryResponseDTO response =
                categoryService.create(dto, owner);

        assertEquals(1L, response.id());
        assertEquals("Alimentação", response.name());
        assertEquals(TransactionType.EXPENSE, response.type());
        assertEquals(3L, response.userId());

        ArgumentCaptor<Category> categoryCaptor =
                ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(categoryCaptor.capture());

        Category savedCategory = categoryCaptor.getValue();

        assertSame(owner, savedCategory.getUser());
        assertEquals("Alimentação", savedCategory.getName());
        assertEquals(TransactionType.EXPENSE, savedCategory.getType());
    }

    @Test
    void shouldCreateIncomeCategory() {

        User owner = new User();
        owner.setId(3L);

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "Salário",
                TransactionType.INCOME
        );

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> {

                    Category category = invocation.getArgument(0);
                    category.setId(2L);

                    return category;
                });

        CategoryResponseDTO response =
                categoryService.create(dto, owner);

        assertEquals(2L, response.id());
        assertEquals("Salário", response.name());
        assertEquals(TransactionType.INCOME, response.type());
        assertEquals(3L, response.userId());

        ArgumentCaptor<Category> categoryCaptor =
                ArgumentCaptor.forClass(Category.class);

        verify(categoryRepository).save(categoryCaptor.capture());

        assertSame(owner, categoryCaptor.getValue().getUser());
        assertEquals(
                TransactionType.INCOME,
                categoryCaptor.getValue().getType()
        );
    }

    @Test
    void shouldListOnlyOwnedCategories() {

        User owner = new User();
        owner.setId(3L);

        Category food = new Category();
        food.setId(1L);
        food.setName("Alimentação");
        food.setType(TransactionType.EXPENSE);
        food.setUser(owner);

        Category salary = new Category();
        salary.setId(2L);
        salary.setName("Salário");
        salary.setType(TransactionType.INCOME);
        salary.setUser(owner);

        when(categoryRepository.findByUser_Id(3L))
                .thenReturn(List.of(food, salary));

        List<CategoryResponseDTO> response =
                categoryService.listOwned(owner);

        assertEquals(2, response.size());

        assertEquals("Alimentação", response.get(0).name());
        assertEquals(TransactionType.EXPENSE, response.get(0).type());
        assertEquals(3L, response.get(0).userId());

        assertEquals("Salário", response.get(1).name());
        assertEquals(TransactionType.INCOME, response.get(1).type());
        assertEquals(3L, response.get(1).userId());

        verify(categoryRepository).findByUser_Id(3L);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoCategories() {

        User owner = new User();
        owner.setId(4L);

        when(categoryRepository.findByUser_Id(4L))
                .thenReturn(List.of());

        List<CategoryResponseDTO> response =
                categoryService.listOwned(owner);

        assertTrue(response.isEmpty());

        verify(categoryRepository).findByUser_Id(4L);
    }
}
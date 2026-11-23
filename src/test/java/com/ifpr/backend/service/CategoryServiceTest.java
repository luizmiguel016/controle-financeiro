
package com.ifpr.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ifpr.backend.dto.CategoryCreationDTO;
import com.ifpr.backend.dto.CategoryResponseDTO;
import com.ifpr.backend.dto.CategoryUpdateDTO;
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

    @Test
    void shouldFindCategoryOwnedByUser() {

        User owner = new User();
        owner.setId(3L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
        category.setType(TransactionType.EXPENSE);
        category.setUser(owner);

        when(categoryRepository.findByIdAndUser_Id(1L, 3L))
                .thenReturn(Optional.of(category));

        Optional<CategoryResponseDTO> response =
                categoryService.findOwnedById(1L, owner);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().id());
        assertEquals("Alimentação", response.get().name());
        assertEquals(TransactionType.EXPENSE, response.get().type());
        assertEquals(3L, response.get().userId());

        verify(categoryRepository).findByIdAndUser_Id(1L, 3L);
    }

    @Test
    void shouldNotFindCategoryOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        when(categoryRepository.findByIdAndUser_Id(1L, 4L))
                .thenReturn(Optional.empty());

        Optional<CategoryResponseDTO> response =
                categoryService.findOwnedById(1L, owner);

        assertTrue(response.isEmpty());

        verify(categoryRepository).findByIdAndUser_Id(1L, 4L);
    }

    @Test
    void shouldUpdateCategoryOwnedByUser() {

        User owner = new User();
        owner.setId(3L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
        category.setType(TransactionType.EXPENSE);
        category.setUser(owner);

        CategoryUpdateDTO dto = new CategoryUpdateDTO(
                "  Supermercado  ",
                TransactionType.EXPENSE
        );

        when(categoryRepository.findByIdAndUser_Id(1L, 3L))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(category))
                .thenReturn(category);

        Optional<CategoryResponseDTO> response =
                categoryService.updateOwned(1L, dto, owner);

        assertTrue(response.isPresent());
        assertEquals(1L, response.get().id());
        assertEquals("Supermercado", response.get().name());
        assertEquals(TransactionType.EXPENSE, response.get().type());
        assertEquals(3L, response.get().userId());

        assertEquals("Supermercado", category.getName());
        assertSame(owner, category.getUser());

        verify(categoryRepository).findByIdAndUser_Id(1L, 3L);
        verify(categoryRepository).save(category);
    }

    @Test
    void shouldNotUpdateCategoryOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        CategoryUpdateDTO dto = new CategoryUpdateDTO(
                "Nome Alterado",
                TransactionType.INCOME
        );

        when(categoryRepository.findByIdAndUser_Id(1L, 4L))
                .thenReturn(Optional.empty());

        Optional<CategoryResponseDTO> response =
                categoryService.updateOwned(1L, dto, owner);

        assertTrue(response.isEmpty());

        verify(categoryRepository).findByIdAndUser_Id(1L, 4L);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void shouldDeleteCategoryOwnedByUser() {

        User owner = new User();
        owner.setId(3L);

        Category category = new Category();
        category.setId(1L);
        category.setName("Alimentação");
        category.setType(TransactionType.EXPENSE);
        category.setUser(owner);

        when(categoryRepository.findByIdAndUser_Id(1L, 3L))
                .thenReturn(Optional.of(category));

        boolean deleted =
                categoryService.deleteOwned(1L, owner);

        assertTrue(deleted);

        verify(categoryRepository).findByIdAndUser_Id(1L, 3L);
        verify(categoryRepository).delete(category);
    }

    @Test
    void shouldNotDeleteCategoryOwnedByAnotherUser() {

        User owner = new User();
        owner.setId(4L);

        when(categoryRepository.findByIdAndUser_Id(1L, 4L))
                .thenReturn(Optional.empty());

        boolean deleted =
                categoryService.deleteOwned(1L, owner);

        assertFalse(deleted);

        verify(categoryRepository).findByIdAndUser_Id(1L, 4L);
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}

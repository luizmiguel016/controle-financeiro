package com.ifpr.backend.dto;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ifpr.backend.entity.TransactionType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class CategoryCreationDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void tearDown() {
        validatorFactory.close();
    }

    @Test
    void shouldAcceptValidCategory() {

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "Alimentação",
                TransactionType.EXPENSE
        );

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectBlankName() {

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "   ",
                TransactionType.EXPENSE
        );

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectNullName() {

        CategoryCreationDTO dto = new CategoryCreationDTO(
                null,
                TransactionType.INCOME
        );

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectNameLongerThan80Characters() {

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "A".repeat(81),
                TransactionType.EXPENSE
        );

        assertFalse(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectMissingType() {

        CategoryCreationDTO dto = new CategoryCreationDTO(
                "Salário",
                null
        );

        assertFalse(validator.validate(dto).isEmpty());
    }
}

package com.ifpr.backend.dto;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.ifpr.backend.entity.TransactionType;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

class TransactionUpdateDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static final LocalDate TEST_DATE =
            LocalDate.of(2026, 11, 26);

    private static final BigDecimal VALID_AMOUNT =
            new BigDecimal("215.50");

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
    void shouldAcceptValidTransactionUpdate() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectMissingDescription() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                null,
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "description");
    }

    @Test
    void shouldRejectBlankDescription() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "   ",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "description");
    }

    @Test
    void shouldRejectDescriptionLongerThan255Characters() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "A".repeat(256),
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "description");
    }

    @Test
    void shouldRejectMissingAmount() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                null,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectZeroAmount() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                BigDecimal.ZERO,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectNegativeAmount() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                new BigDecimal("-25.00"),
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectAmountWithMoreThanTwoDecimalPlaces() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                new BigDecimal("215.999"),
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectAmountWithMoreThanThirteenIntegerDigits() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                new BigDecimal("10000000000000.00"),
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectMissingType() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                null,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "type");
    }

    @Test
    void shouldRejectMissingTransactionDate() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                null,
                1L,
                2L
        );

        assertFieldInvalid(dto, "transactionDate");
    }

    @Test
    void shouldRejectMissingWalletId() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                null,
                2L
        );

        assertFieldInvalid(dto, "walletId");
    }

    @Test
    void shouldRejectNonPositiveWalletId() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                0L,
                2L
        );

        assertFieldInvalid(dto, "walletId");
    }

    @Test
    void shouldRejectMissingCategoryId() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                null
        );

        assertFieldInvalid(dto, "categoryId");
    }

    @Test
    void shouldRejectNonPositiveCategoryId() {

        TransactionUpdateDTO dto = new TransactionUpdateDTO(
                "Compra mensal",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                -1L
        );

        assertFieldInvalid(dto, "categoryId");
    }

    private void assertFieldInvalid(
            TransactionUpdateDTO dto,
            String fieldName) {

        boolean hasViolation = validator.validate(dto)
                .stream()
                .anyMatch(violation ->
                        violation.getPropertyPath()
                                .toString()
                                .equals(fieldName)
                );

        assertTrue(hasViolation);
    }
}

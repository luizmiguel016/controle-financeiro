
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

class TransactionCreationDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    private static final LocalDate TEST_DATE =
            LocalDate.of(2026, 11, 23);

    private static final BigDecimal VALID_AMOUNT =
            new BigDecimal("189.90");

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
    void shouldAcceptValidTransaction() {

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void shouldRejectBlankDescription() {

        TransactionCreationDTO dto = new TransactionCreationDTO(
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
                new BigDecimal("-50.00"),
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectAmountWithMoreThanTwoDecimalPlaces() {

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
                new BigDecimal("19.999"),
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                2L
        );

        assertFieldInvalid(dto, "amount");
    }

    @Test
    void shouldRejectAmountWithMoreThanThirteenIntegerDigits() {

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
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

        TransactionCreationDTO dto = new TransactionCreationDTO(
                "Compra no supermercado",
                VALID_AMOUNT,
                TransactionType.EXPENSE,
                TEST_DATE,
                1L,
                -1L
        );

        assertFieldInvalid(dto, "categoryId");
    }

    private void assertFieldInvalid(
            TransactionCreationDTO dto,
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

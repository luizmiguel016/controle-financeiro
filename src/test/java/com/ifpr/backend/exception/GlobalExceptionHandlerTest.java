package com.ifpr.backend.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldReturn409ForConflict() {

        ConflictException exception =
                new ConflictException("E-mail já cadastrado");

        ResponseEntity<ErrorResponse> response =
                handler.handleConflict(exception);

        assertEquals(
                HttpStatus.CONFLICT,
                response.getStatusCode()
        );

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(409, body.status());
        assertEquals("Conflict", body.error());
        assertEquals("E-mail já cadastrado", body.message());
    }

    @Test
    void shouldReturn422ForBusinessRuleViolation() {

        BusinessException exception =
                new BusinessException("Operação não permitida");

        ResponseEntity<ErrorResponse> response =
                handler.handleBusinessException(exception);

        assertEquals(
                HttpStatus.UNPROCESSABLE_ENTITY,
                response.getStatusCode()
        );

        ErrorResponse body = response.getBody();

        assertNotNull(body);
        assertEquals(422, body.status());
        assertEquals("Unprocessable Entity", body.error());
        assertEquals("Operação não permitida", body.message());
    }
}
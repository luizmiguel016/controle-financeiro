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
    void deveRetornar409ParaConflito() {

        ConflitoException excecao = new ConflitoException("E-mail já cadastrado");

        ResponseEntity<ErroResposta> resposta = handler.tratarConflito(excecao);

        assertEquals(HttpStatus.CONFLICT, resposta.getStatusCode());

        ErroResposta corpo = resposta.getBody();

        assertNotNull(corpo);
        assertEquals(409, corpo.status());
        assertEquals("Conflict", corpo.error());
        assertEquals("E-mail já cadastrado", corpo.message());
    }

    @Test
    void deveRetornar422ParaRegraDeNegocio() {

        BusinessException excecao = new BusinessException("Operação não permitida");

        ResponseEntity<ErroResposta> resposta = handler.tratarBusinessException(excecao);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, resposta.getStatusCode());

        ErroResposta corpo = resposta.getBody();

        assertNotNull(corpo);
        assertEquals(422, corpo.status());
        assertEquals("Unprocessable Entity", corpo.error());
        assertEquals("Operação não permitida", corpo.message());
    }
}
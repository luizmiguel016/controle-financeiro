package com.ifpr.backend.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErroResposta> tratarBusinessException(BusinessException ex) {

        return criarResposta(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErroResposta> tratarCredenciaisInvalidas(BadCredentialsException ex) {
        
        return criarResposta(HttpStatus.UNAUTHORIZED, "E-mail ou senha inválidos");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarValidacao(MethodArgumentNotValidException ex) {

        String mensagem = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(erro -> erro.getDefaultMessage())
                .orElse("Dados inválidos");

        return criarResposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarException(Exception ex) {

        return criarResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno do servidor");
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> tratarConflito(ConflitoException ex) {
        
        return criarResposta(HttpStatus.CONFLICT, ex.getMessage());
    }

    private ResponseEntity<ErroResposta> criarResposta(HttpStatus status, String mensagem) {

        ErroResposta erro = new ErroResposta(LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem);

        return ResponseEntity
                .status(status)
                .body(erro);
    }
}
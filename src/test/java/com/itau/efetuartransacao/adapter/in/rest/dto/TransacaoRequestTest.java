package com.itau.efetuartransacao.adapter.in.rest.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.validation.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TransacaoRequestTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testTransacaoRequestValido() {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("12345-6");
        request.setIdContaDestino("98765-4");
        request.setValor(100.0);

        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testIdContaOrigemObrigatorio() {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem(""); // inválido
        request.setIdContaDestino("98765-4");
        request.setValor(100.0);

        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("idContaOrigem")));
    }

    @Test
    public void testIdContaDestinoObrigatorio() {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("12345-6");
        request.setIdContaDestino("  "); // inválido
        request.setValor(100.0);

        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("idContaDestino")));
    }

    @Test
    public void testValorPositivo() {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("12345-6");
        request.setIdContaDestino("98765-4");
        request.setValor(-50.0); // inválido

        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("valor")));
    }

    @Test
    public void testValorNaoPodeSerNulo() {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("12345-6");
        request.setIdContaDestino("98765-4");
        request.setValor(null); // inválido

        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("valor")));
    }
}

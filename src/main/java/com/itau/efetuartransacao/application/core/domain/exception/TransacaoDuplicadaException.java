package com.itau.efetuartransacao.application.core.domain.exception;

public class TransacaoDuplicadaException extends RuntimeException {
    public TransacaoDuplicadaException(String idempotencyKey) {
        super("Transacao com chave " + idempotencyKey + " ja foi processada.");
    }
}

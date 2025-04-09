package com.itau.efetuartransacao.application.core.domain.exception;

public class LimiteExcedidoException extends RuntimeException {
    public LimiteExcedidoException(String mensagem) {
        super(mensagem);
    }
}

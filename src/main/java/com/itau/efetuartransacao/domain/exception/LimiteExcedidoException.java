package com.itau.efetuartransacao.domain.exception;

public class LimiteExcedidoException extends RuntimeException {
    public LimiteExcedidoException(String mensagem) {
        super(mensagem);
    }
}

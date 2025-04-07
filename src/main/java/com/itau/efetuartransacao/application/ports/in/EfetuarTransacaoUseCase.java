package com.itau.efetuartransacao.application.ports.in;

import com.itau.efetuartransacao.domain.model.Transacao;

public interface EfetuarTransacaoUseCase {
    Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor);
}

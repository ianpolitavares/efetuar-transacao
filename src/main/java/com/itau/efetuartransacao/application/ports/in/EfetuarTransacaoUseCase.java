package com.itau.efetuartransacao.application.ports.in;

import com.itau.efetuartransacao.application.core.domain.model.Transacao;

public interface EfetuarTransacaoUseCase {
    Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor, String idempotencyKey);
}

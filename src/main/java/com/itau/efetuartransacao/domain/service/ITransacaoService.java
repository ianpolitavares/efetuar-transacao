package com.itau.efetuartransacao.domain.service;

import com.itau.efetuartransacao.domain.model.Transacao;

public interface ITransacaoService {
    Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor);
}

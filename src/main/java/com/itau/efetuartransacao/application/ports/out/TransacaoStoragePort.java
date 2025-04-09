package com.itau.efetuartransacao.application.ports.out;

import com.itau.efetuartransacao.application.core.domain.model.Transacao;

public interface TransacaoStoragePort {
    void save(Transacao transacao);
}

package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class TransacaoStorageAdapter implements TransacaoStoragePort {

    private final TransacaoJpaRepository repository;

    public TransacaoStorageAdapter(TransacaoJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void save(Transacao transacao) {
        TransacaoEntity entity = new TransacaoEntity(
                transacao.getIdTransacao(),
                transacao.getIdContaOrigem(),
                transacao.getIdContaDestino(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getStatus()
        );
        repository.save(entity);
    }
}

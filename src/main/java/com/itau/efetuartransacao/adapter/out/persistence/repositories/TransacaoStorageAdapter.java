package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
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
        TransacaoEntity entity = new TransacaoEntity();
        entity.setIdTransacao(transacao.getIdTransacao());
        entity.setIdContaOrigem(transacao.getIdContaOrigem());
        entity.setIdContaDestino(transacao.getIdContaDestino());
        entity.setValor(transacao.getValor());
        entity.setDataHora(transacao.getDataHora());
        entity.setStatus(transacao.getStatus());
        repository.save(entity);
    }
}

package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import com.itau.efetuartransacao.domain.model.Transacao;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
* Classe criara para armazenar a transacao local adhoc,
* Em um cenário real, aqui ficaria a implementação com o banco de dados
* Por exemplo: TransacaoRepository extends JpaRepository
* */

public class TransacaoRepository implements TransacaoStoragePort {

    private final Map<String, Transacao> transacoesMap = new ConcurrentHashMap<>();

    public void save(Transacao transacao) {
        transacoesMap.put(transacao.getIdTransacao(), transacao);
    }

    public Transacao findById(String idTransacao) {
        return transacoesMap.get(idTransacao);
    }
}

package com.itau.efetuartransacao.infra.repository;

import com.itau.efetuartransacao.domain.model.Transacao;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/*
* Classe criara para armazenar a transacao local adhoc,
* Em um cenário real, aqui ficaria a implementação com o banco de dados
* Por exemplo: TransacaoRepository extends JpaRepository
* */
@Repository
public class TransacaoRepository {

    private final Map<String, Transacao> transacoesMap = new HashMap<>();

    public void save(Transacao transacao) {
        transacoesMap.put(transacao.getIdTransacao(), transacao);
    }

    public Transacao findById(String idTransacao) {
        return transacoesMap.get(idTransacao);
    }
}

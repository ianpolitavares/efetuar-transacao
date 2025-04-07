package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class TransacaoRepositoryTest {

    private TransacaoRepository repository;

    @BeforeEach
    public void setup() {
        repository = new TransacaoRepository();
    }

    @Test
    public void testSaveAndFindById() {
        String id = UUID.randomUUID().toString();

        Transacao transacao = new Transacao();
        transacao.setIdTransacao(id);
        transacao.setIdContaOrigem("123");
        transacao.setIdContaDestino("456");
        transacao.setValor(100.0);
        transacao.setStatus(TransacaoStatus.CONCLUIDA);
        transacao.setDataHora(LocalDateTime.now());

        repository.save(transacao);

        Transacao encontrada = repository.findById(id);

        assertNotNull(encontrada);
        assertEquals("123", encontrada.getIdContaOrigem());
        assertEquals("456", encontrada.getIdContaDestino());
        assertEquals(100.0, encontrada.getValor());
        assertEquals(TransacaoStatus.CONCLUIDA, encontrada.getStatus());
    }

    @Test
    public void testFindByIdNotFound() {
        Transacao resultado = repository.findById("id-inexistente");
        assertNull(resultado);
    }

    @Test
    public void testSobrescreverTransacaoComMesmoId() {
        String id = "transacao-duplicada";

        Transacao t1 = new Transacao(id, "1", "2", 50.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA);
        Transacao t2 = new Transacao(id, "1", "2", 75.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA);

        repository.save(t1);
        repository.save(t2); // sobrescreve

        Transacao atual = repository.findById(id);

        assertNotNull(atual);
        assertEquals(75.0, atual.getValor());
    }
}

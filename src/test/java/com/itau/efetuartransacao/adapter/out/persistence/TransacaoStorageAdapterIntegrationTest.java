package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TransacaoStorageAdapterIntegrationTest {

    @Autowired
    private TransacaoJpaRepository jpaRepository;

    private TransacaoStoragePort repository;

    @BeforeEach
    void setUp() {
        repository = new TransacaoStorageAdapter(jpaRepository);
    }

    @Test
    void testSalvarTransacao() {
        UUID id = UUID.randomUUID();

        Transacao transacao = new Transacao(id, "123", "456", 100.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA);
        repository.save(transacao);

        assertTrue(jpaRepository.findById(id).isPresent());
    }


    @Test
    void testSobrescreverTransacaoComMesmoId() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");

        Transacao t1 = new Transacao(id, "1", "2", 50.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA);
        Transacao t2 = new Transacao(id, "1", "2", 75.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA);

        repository.save(t1);
        repository.save(t2);

        TransacaoEntity entity = jpaRepository.findById(id).orElse(null); // <-- corrigido aqui
        assertNotNull(entity);
        assertEquals(75.0, entity.getValor());
    }

}

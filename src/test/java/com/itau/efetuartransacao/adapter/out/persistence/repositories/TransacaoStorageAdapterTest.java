package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.TransacaoEntity;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TransacaoStorageAdapterTest {

    private TransacaoJpaRepository jpaRepository;
    private TransacaoStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(TransacaoJpaRepository.class);
        adapter = new TransacaoStorageAdapter(jpaRepository);
    }

    @Test
    void testSaveShouldMapAndCallRepository() {
        UUID id = UUID.randomUUID();
        Transacao transacao = new Transacao(
                id, "1", "2", 100.0, LocalDateTime.now(), TransacaoStatus.CONCLUIDA
        );

        adapter.save(transacao);

        ArgumentCaptor<TransacaoEntity> captor = ArgumentCaptor.forClass(TransacaoEntity.class);
        verify(jpaRepository).save(captor.capture());

        TransacaoEntity saved = captor.getValue();
        assertEquals(transacao.getIdTransacao(), saved.getIdTransacao());
        assertEquals(transacao.getIdContaOrigem(), saved.getIdContaOrigem());
        assertEquals(transacao.getIdContaDestino(), saved.getIdContaDestino());
        assertEquals(transacao.getValor(), saved.getValor());
        assertEquals(transacao.getDataHora(), saved.getDataHora());
        assertEquals(transacao.getStatus(), saved.getStatus());
    }
}

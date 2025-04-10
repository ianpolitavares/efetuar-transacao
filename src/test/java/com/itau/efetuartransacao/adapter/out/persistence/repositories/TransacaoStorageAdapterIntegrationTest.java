package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransacaoStorageAdapterIntegrationTest {

    @Mock
    private TransacaoJpaRepository jpaRepository;

    private TransacaoStorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new TransacaoStorageAdapter(jpaRepository);
    }

    @Test
    void testSalvarTransacaoDeveChamarJpaRepositorySave() {
        // Arrange
        UUID id = UUID.randomUUID();
        Transacao transacao = new Transacao(
                id,
                "123",
                "456",
                100.0,
                LocalDateTime.now(),
                TransacaoStatus.CONCLUIDA
        );

        // Act
        adapter.save(transacao);

        // Assert
        verify(jpaRepository).save(any()); // verifica se o método save foi chamado
    }
}

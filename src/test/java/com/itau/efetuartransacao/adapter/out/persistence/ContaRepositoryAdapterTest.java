package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import com.itau.efetuartransacao.adapter.out.persistence.repositories.ContaJpaRepository;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ContaRepositoryAdapterTest {

    private ContaJpaRepository jpaRepository;
    private ContaRepositoryAdapter adapter;

    @BeforeEach
    void setup() {
        jpaRepository = mock(ContaJpaRepository.class);
        adapter = new ContaRepositoryAdapter(jpaRepository);
    }

    @Test
    void testFindByIdComContaExistente() {
        // Arrange
        ContaEntity entity = new ContaEntity("123", 100.0, 500.0, 0.0);
        when(jpaRepository.findById("123")).thenReturn(Optional.of(entity));

        // Act
        Conta conta = adapter.findById("123");

        // Assert
        assertNotNull(conta);
        assertEquals("123", conta.getIdConta());
        assertEquals(100.0, conta.getSaldo());
        assertEquals(500.0, conta.getLimite());
        assertEquals(0.0, conta.getLimiteUtilizado());
    }

    @Test
    void testFindByIdComContaInexistente() {
        // Arrange
        when(jpaRepository.findById("999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContaNaoEncontradaException.class, () -> adapter.findById("999"));
    }

    @Test
    void testUpdateSalvaContaCorretamente() {
        // Arrange
        Conta conta = new Conta("456", 200.0, 400.0, 50.0);

        // Act
        adapter.update(conta);

        // Assert
        ArgumentCaptor<ContaEntity> captor = ArgumentCaptor.forClass(ContaEntity.class);
        verify(jpaRepository, times(1)).save(captor.capture());

        ContaEntity salvo = captor.getValue();
        assertEquals("456", salvo.getIdConta());
        assertEquals(200.0, salvo.getSaldo());
        assertEquals(400.0, salvo.getLimite());
        assertEquals(50.0, salvo.getLimiteUtilizado());
    }
}

package com.itau.efetuartransacao.application.service;

import com.itau.efetuartransacao.adapter.out.mongodb.repository.IdempotencyKeyRepository;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.application.core.domain.exception.TransacaoDuplicadaException;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EfetuarTransacaoServiceTest {

    @Mock
    private ContaPort contaPort;

    @Mock
    private TransacaoStoragePort transacaoStoragePort;

    @Mock
    private IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Timer timer;

    private EfetuarTransacaoService transacaoService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        contaOrigem = new Conta("1", 100.0, 50.0, 0.0);
        contaDestino = new Conta("2", 200.0, 100.0, 0.0);

        when(meterRegistry.timer(anyString())).thenReturn(timer);
        when(timer.record(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });

        when(idempotencyKeyRepository.existsById("teste-key")).thenReturn(false);
        when(idempotencyKeyRepository.save(any())).thenReturn(null);

        transacaoService = new EfetuarTransacaoService(
                contaPort,
                transacaoStoragePort,
                meterRegistry,
                idempotencyKeyRepository
        );
        transacaoService.initMetrics();
    }


    @Test
    public void testTransacaoComSucesso() {
        when(contaPort.findById("1")).thenReturn(contaOrigem);
        when(contaPort.findById("2")).thenReturn(contaDestino);

        Transacao transacao = transacaoService.efetuarTransacao("1", "2", 120.0, "teste-key");

        assertEquals(TransacaoStatus.CONCLUIDA, transacao.getStatus());
        assertEquals("1", transacao.getIdContaOrigem());
        assertEquals("2", transacao.getIdContaDestino());
        assertEquals(120.0, transacao.getValor());
        assertNotNull(transacao.getIdTransacao());
        assertNotNull(transacao.getDataHora());
        assertEquals(0.0, contaOrigem.getSaldo());
        assertEquals(320.0, contaDestino.getSaldo());

        verify(transacaoStoragePort, times(1)).save(any());
        verify(contaPort, times(1)).update(contaOrigem);
        verify(contaPort, times(1)).update(contaDestino);
    }

    @Test
    public void testContaOrigemNaoEncontrada() {
        when(contaPort.findById("1")).thenReturn(null);
        when(contaPort.findById("2")).thenReturn(contaDestino);

        assertThrows(ContaNaoEncontradaException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 50.0, "teste-key"));
    }

    @Test
    public void testContaDestinoNaoEncontrada() {
        when(contaPort.findById("1")).thenReturn(contaOrigem);
        when(contaPort.findById("2")).thenReturn(null);

        assertThrows(ContaNaoEncontradaException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 50.0, "teste-key"));
    }

    @Test
    public void testSaldoInsuficiente() {
        when(contaPort.findById("1")).thenReturn(contaOrigem);
        when(contaPort.findById("2")).thenReturn(contaDestino);

        assertThrows(SaldoInsuficienteException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 1000.0, "teste-key"));
    }

    @Test
    public void testFalhaDuranteTransacao() {
        when(contaPort.findById("1")).thenReturn(contaOrigem);
        when(contaPort.findById("2")).thenReturn(contaDestino);
        doThrow(new RuntimeException("Erro interno")).when(contaPort).update(contaOrigem);

        assertThrows(RuntimeException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 50.0, "teste-key"));

        verify(contaPort).update(contaOrigem);
        verify(contaPort, never()).update(contaDestino);
        verify(transacaoStoragePort, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoIdempotencyKeyJaFoiProcessada() {
        // Arrange
        String idContaOrigem = "12345-6";
        String idContaDestino = "98765-4";
        Double valor = 100.0;
        String idempotencyKey = "duplicada-123";

        when(idempotencyKeyRepository.existsById(idempotencyKey)).thenReturn(true);

        // Act & Assert
        TransacaoDuplicadaException exception = assertThrows(
                TransacaoDuplicadaException.class,
                () -> transacaoService.efetuarTransacao(idContaOrigem, idContaDestino, valor, idempotencyKey)
        );

        assertEquals("Transacao com chave duplicada-123 ja foi processada.", exception.getMessage());
        verify(idempotencyKeyRepository).existsById(idempotencyKey);
        verifyNoInteractions(contaPort);
        verifyNoInteractions(transacaoStoragePort);
    }


}

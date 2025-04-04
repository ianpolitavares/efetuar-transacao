package com.itau.efetuartransacao.domain.service;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.infra.repository.TransacaoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransacaoServiceTest {

    @Mock
    private IContaProvider contaProvider;

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private MeterRegistry meterRegistry;

    @Mock
    private Timer timer;

    @InjectMocks
    private TransacaoService transacaoService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        contaOrigem = new Conta("1", 100.0, 50.0);
        contaDestino = new Conta("2", 200.0, 100.0);

        when(meterRegistry.timer(anyString())).thenReturn(timer);
        when(timer.record(any(Supplier.class))).thenAnswer(invocation -> {
            Supplier<?> supplier = invocation.getArgument(0);
            return supplier.get();
        });
        transacaoService.initMetrics();
    }

    @Test
    public void testTransacaoComSucesso() {
        when(contaProvider.findById("1")).thenReturn(contaOrigem);
        when(contaProvider.findById("2")).thenReturn(contaDestino);

        Transacao transacao = transacaoService.efetuarTransacao("1", "2", 120.0);

        assertEquals(TransacaoStatus.CONCLUIDA, transacao.getStatus());
        assertEquals("1", transacao.getIdContaOrigem());
        assertEquals("2", transacao.getIdContaDestino());
        assertEquals(120.0, transacao.getValor());
        assertNotNull(transacao.getIdTransacao());
        assertNotNull(transacao.getDataHora());
        assertEquals(0.0, contaOrigem.getSaldo());
        assertEquals(320.0, contaDestino.getSaldo()); // considerando valor = 120.0


        verify(transacaoRepository, times(1)).save(any());
        verify(contaProvider, times(1)).update(contaOrigem);
        verify(contaProvider, times(1)).update(contaDestino);
    }

    @Test
    public void testContaOrigemNaoEncontrada() {
        when(contaProvider.findById("2")).thenReturn(contaDestino);
        when(contaProvider.findById("1")).thenReturn(null);

        assertThrows(ContaNaoEncontradaException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 50.0));
    }

    @Test
    public void testContaDestinoNaoEncontrada() {
        when(contaProvider.findById("1")).thenReturn(contaOrigem);
        when(contaProvider.findById("2")).thenReturn(null);

        assertThrows(ContaNaoEncontradaException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 50.0));
    }

    @Test
    public void testSaldoInsuficiente() {
        when(contaProvider.findById("1")).thenReturn(contaOrigem);
        when(contaProvider.findById("2")).thenReturn(contaDestino);

        assertThrows(SaldoInsuficienteException.class, () ->
                transacaoService.efetuarTransacao("1", "2", 1000.0));
    }

    @Test
    public void testFalhaDuranteTransacao() {
        when(contaProvider.findById("1")).thenReturn(contaOrigem);
        when(contaProvider.findById("2")).thenReturn(contaDestino);

        // Simula falha na primeira atualização
        doThrow(new RuntimeException("Erro interno")).when(contaProvider).update(contaOrigem);

        Transacao transacao = transacaoService.efetuarTransacao("1", "2", 50.0);

        assertEquals(TransacaoStatus.CONCLUIDA, transacao.getStatus());

        verify(contaProvider, times(1)).update(contaOrigem);
        verify(contaProvider, never()).update(contaDestino); // 💡 ajuste aqui!
        verify(transacaoRepository, times(1)).save(any());
    }

}

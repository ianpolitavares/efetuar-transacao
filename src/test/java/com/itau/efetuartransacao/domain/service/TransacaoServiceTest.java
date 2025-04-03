package com.itau.efetuartransacao.domain.service;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.infra.repository.TransacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransacaoServiceTest {

    @Mock
    private IContaProvider contaProvider;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        contaOrigem = new Conta("1", 100.0, 50.0);
        contaDestino = new Conta("2", 200.0, 100.0);
    }

    @Test
    public void testTransacaoComSucesso() {
        when(contaProvider.findById("1")).thenReturn(contaOrigem);
        when(contaProvider.findById("2")).thenReturn(contaDestino);

        Transacao transacao = transacaoService.efetuarTransacao("1", "2", 120.0);

        assertEquals(TransacaoStatus.CONCLUIDA, transacao.getStatus());
        assertEquals("1", transacao.getIdContaOrigem());
        assertEquals("2", transacao.getIdContaDestino());
        verify(transacaoRepository, times(1)).save(any());
    }

    @Test
    public void testContaOrigemNaoEncontrada() {
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
        doThrow(new RuntimeException("Erro interno")).when(contaProvider).update(any());

        Transacao transacao = transacaoService.efetuarTransacao("1", "2", 50.0);

        assertEquals(TransacaoStatus.CONCLUIDA, transacao.getStatus()); // Mesmo com erro, o sistema força status final
        verify(transacaoRepository, times(1)).save(any());
    }
}

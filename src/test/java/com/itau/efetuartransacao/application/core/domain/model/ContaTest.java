package com.itau.efetuartransacao.application.core.domain.model;

import com.itau.efetuartransacao.application.core.domain.model.Conta;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContaTest {

    @Test
    public void testConstrutorVazio() {
        Conta conta = new Conta(); // chama o NoArgsConstructor
        assertNotNull(conta);
    }

    @Test
    public void testPodeEfetuarTransacaoComSaldo() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        assertTrue(conta.podeEfetuarTransacao(80.0));
    }

    @Test
    public void testPodeEfetuarTransacaoComSaldoEMaisLimite() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        assertTrue(conta.podeEfetuarTransacao(140.0));
    }

    @Test
    public void testNaoPodeEfetuarTransacaoValorMaiorQueSaldoMaisLimite() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        assertFalse(conta.podeEfetuarTransacao(200.0));
    }

    @Test
    public void testDebitoSomenteDoSaldo() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        conta.debitar(80.0);
        assertEquals(20.0, conta.getSaldo());
        assertEquals(50.0, conta.getLimite());
    }

    @Test
    public void testDebitoUsandoLimite() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        conta.debitar(120.0);
        assertEquals(0.0, conta.getSaldo());                   // saldo foi todo usado
        assertEquals(50.0, conta.getLimite());                 // limite continua o mesmo
        assertEquals(20.0, conta.getLimiteUtilizado());        // usou 20 do limite

    }

    @Test
    public void testDebitoExatamenteSaldoMaisLimite() {
        Conta conta = new Conta("1", 100.0, 50.0, 0.0);
        conta.debitar(150.0);
        assertEquals(0.0, conta.getSaldo());
        assertEquals(50.0, conta.getLimite());
        assertEquals(50.0, conta.getLimiteUtilizado());

    }

    @Test
    public void testDebitoMaiorQueSaldoMaisLimiteNaoModifica() {
        Conta conta = new Conta("1", 100.0, 50.0,0.0);

        assertFalse(conta.podeEfetuarTransacao(200.0));

        // Não debita — apenas garante que nada mudou
        assertEquals(100.0, conta.getSaldo());
        assertEquals(50.0, conta.getLimite());
    }


    @Test
    public void testPodeEfetuarTransacaoExatamenteSaldoMaisLimite() {
        Conta conta = new Conta("1", 100.0, 50.0,0.0);
        assertTrue(conta.podeEfetuarTransacao(150.0));
    }

    @Test
    public void testDebitoExatamenteIgualAoSaldo() {
        Conta conta = new Conta("1", 100.0, 50.0,0.0);
        conta.debitar(100.0); // valor == saldo

        assertEquals(0.0, conta.getSaldo(), 0.001);
        assertEquals(50.0, conta.getLimite(), 0.001); // limite não deve ser usado
    }

}

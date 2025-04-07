package com.itau.efetuartransacao.infra.provider;

import com.itau.efetuartransacao.domain.model.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContaProviderImplTest {

    private ContaProviderImpl contaProvider;

    @BeforeEach
    public void setUp() {
        contaProvider = new ContaProviderImpl();
        contaProvider.init();
    }

    @Test
    public void testFindById_existente() {
        Conta conta = contaProvider.findById("12345-6");
        assertNotNull(conta);
        assertEquals("12345-6", conta.getIdConta());
        assertEquals(1000.0, conta.getSaldo());
        assertEquals(500.0, conta.getLimite());
    }

    @Test
    public void testFindById_inexistente() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            contaProvider.findById("00000-0");
        });
        assertTrue(exception.getMessage().contains("Conta não encontrada"));
    }

    @Test
    public void testUpdateConta() {
        Conta novaConta = new Conta("99999-9", 300.0, 200.0);
        contaProvider.update(novaConta);

        Conta recuperada = contaProvider.findById("99999-9");
        assertNotNull(recuperada);
        assertEquals(300.0, recuperada.getSaldo());
        assertEquals(200.0, recuperada.getLimite());
    }
}

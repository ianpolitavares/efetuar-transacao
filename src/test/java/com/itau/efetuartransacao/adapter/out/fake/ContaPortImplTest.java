package com.itau.efetuartransacao.adapter.out.fake;

import com.itau.efetuartransacao.domain.model.Conta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ContaPortImplTest {

    private ContaPortImpl contaProvider;

    @BeforeEach
    public void setUp() {
        contaProvider = new ContaPortImpl();
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
        Conta novaConta = new Conta("99999-9", 300.0, 200.0,0.0);
        contaProvider.update(novaConta);

        Conta recuperada = contaProvider.findById("99999-9");
        assertNotNull(recuperada);
        assertEquals(300.0, recuperada.getSaldo());
        assertEquals(200.0, recuperada.getLimite());
    }

    @Test
    public void testFallbackFindById() {
        ContaPortImpl contaPort = new ContaPortImpl();
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                contaPort.fallbackFindById("123", new RuntimeException("Erro simulado"))
        );

        assertEquals("Serviço de contas indisponível no momento", ex.getMessage());
    }

    @Test
    public void testFallbackUpdate() {
        ContaPortImpl contaPort = new ContaPortImpl();
        Conta conta = new Conta("123", 100.0, 50.0,0.0);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                contaPort.fallbackUpdate(conta, new RuntimeException("Erro simulado"))
        );

        assertEquals("Não foi possível atualizar a conta no momento", ex.getMessage());
    }

}

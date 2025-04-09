package com.itau.efetuartransacao.adapter.out.fake;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ContaPortImpl implements ContaPort {

    private final Map<String, Conta> contasMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Conta conta1 = new Conta("12345-6", 1000.0, 500.0);
        Conta conta2 = new Conta("98765-4", 2000.0, 1000.0);

        contasMap.put(conta1.getIdConta(), conta1);
        contasMap.put(conta2.getIdConta(), conta2);
    }

    @Override
    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackFindById")
    public Conta findById(String idConta) {
        log.info("Simulando chamada externa para buscar conta {}", idConta);
        Conta conta = contasMap.get(idConta);
        if (conta == null) {
            throw new RuntimeException("Conta não encontrada no provedor externo");
        }
        return conta;
    }

    @Override
    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackUpdate")
    public void update(Conta conta) {
        log.info("Simulando atualização de conta {}", conta.getIdConta());
        contasMap.put(conta.getIdConta(), conta);
    }

    public void fallbackFindById(String idConta, Throwable t) {
        log.error("Fallback ativado para findById: {}", t.getMessage());
        throw new RuntimeException("Serviço de contas indisponível no momento");
    }

    public void fallbackUpdate(Conta conta, Throwable t) {
        log.error("Fallback ativado para update da conta {}: {}", conta.getIdConta(), t.getMessage());
        throw new RuntimeException("Não foi possível atualizar a conta no momento");
    }
}
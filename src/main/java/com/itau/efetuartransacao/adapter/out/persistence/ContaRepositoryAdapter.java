package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import com.itau.efetuartransacao.domain.model.Conta;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
public class ContaRepositoryAdapter implements ContaPort {

    private final ContaJpaRepository repository;

    public ContaRepositoryAdapter(ContaJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackFindById")
    public Conta findById(String idConta) {
        log.info("Buscnado conta {}", idConta);
        ContaEntity entity = repository.findById(idConta)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        return new Conta(entity.getIdConta(), entity.getSaldo(), entity.getLimite());
    }

    @Override
    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackUpdate")
    public void update(Conta conta) {
        log.info("Atualizando conta {}", conta.getIdConta());
        ContaEntity entity = new ContaEntity(conta.getIdConta(), conta.getSaldo(), conta.getLimite());
        repository.save(entity);
    }
}

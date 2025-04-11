package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
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

    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider")
    @Override
    public Conta findById(String idConta) {
        log.info("Buscando conta {}", idConta);

        return repository.findById(idConta)
                .map(entity -> new Conta(
                        entity.getIdConta(),
                        entity.getSaldo(),
                        entity.getLimite(),
                        entity.getLimiteUtilizado()
                ))
                .orElseThrow(() -> new ContaNaoEncontradaException(idConta));
    }

    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackUpdate")
    @Override
    public void update(Conta conta) {
        log.info("Atualizando conta {}", conta.getIdConta());

        ContaEntity entity = repository.findById(conta.getIdConta())
                .orElseThrow(() -> new ContaNaoEncontradaException(conta.getIdConta()));

        entity.setSaldo(conta.getSaldo());
        entity.setLimite(conta.getLimite());
        entity.setLimiteUtilizado(conta.getLimiteUtilizado());

        repository.save(entity);
    }

    public void fallbackUpdate(Conta conta, Throwable t) {
        log.error("Fallback ativado para update da conta {}: {}", conta.getIdConta(), t.getMessage());
        throw new RuntimeException("Nao foi possivel atualizar a conta no momento");
    }
}

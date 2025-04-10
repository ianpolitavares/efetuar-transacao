package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
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
    @CircuitBreaker(
            name = "contaProvider",
            fallbackMethod = "fallbackFindById"
    )
    public Conta findById(String idConta) {
        log.info("Buscando conta {}", idConta);
        ContaEntity entity = repository.findById(idConta)
                .orElseThrow(() -> new ContaNaoEncontradaException(idConta));

        return new Conta(
                entity.getIdConta(),
                entity.getSaldo(),
                entity.getLimite(),
                entity.getLimiteUtilizado()
        );
    }


    @Override
    @Retry(name = "contaProvider")
    @CircuitBreaker(name = "contaProvider", fallbackMethod = "fallbackUpdate")
    public void update(Conta conta) {
        log.info("Atualizando conta {}", conta.getIdConta());
        ContaEntity entity = new ContaEntity(
                conta.getIdConta(),
                conta.getSaldo(),
                conta.getLimite(),
                conta.getLimiteUtilizado()
        );
        repository.save(entity);
    }

    public Conta fallbackFindById(String idConta, Throwable t) {
        log.error("Fallback ativado para findById: {}", t.getMessage());
        throw new RuntimeException("Serviço de contas indisponível");
    }

    public void fallbackUpdate(Conta conta, Throwable t) {
        log.error("Fallback ativado para update da conta {}: {}", conta.getIdConta(), t.getMessage());
        throw new RuntimeException("Nao foi possível atualizar a conta no momento");
    }
}

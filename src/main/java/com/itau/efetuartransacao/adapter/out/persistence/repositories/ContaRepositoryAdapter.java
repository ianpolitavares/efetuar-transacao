package com.itau.efetuartransacao.adapter.out.persistence.repositories;

import com.itau.efetuartransacao.adapter.out.persistence.entity.ContaEntity;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
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
    public void update(Conta conta) {
        log.info("Atualizando conta {}", conta.getIdConta());

        ContaEntity entity = repository.findById(conta.getIdConta())
                .orElseThrow(() -> new ContaNaoEncontradaException(conta.getIdConta()));

        // Atualizações protegidas por versionamento
        entity.setSaldo(conta.getSaldo());
        entity.setLimite(conta.getLimite());
        entity.setLimiteUtilizado(conta.getLimiteUtilizado());

        // Aqui o Hibernate usará o @Version para garantir o bloqueio otimista
        repository.save(entity);
    }
}

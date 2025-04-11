package com.itau.efetuartransacao.application.service;

import com.itau.efetuartransacao.adapter.out.mongodb.entity.IdempotencyKey;
import com.itau.efetuartransacao.adapter.out.mongodb.repository.IdempotencyKeyRepository;
import com.itau.efetuartransacao.application.core.domain.exception.TransacaoDuplicadaException;
import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import com.itau.efetuartransacao.application.core.domain.model.Conta;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.exception.SaldoInsuficienteException;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class EfetuarTransacaoService implements EfetuarTransacaoUseCase {

    private final ContaPort contaPort;
    private final TransacaoStoragePort transacaoStoragePort;
    private final MeterRegistry meterRegistry;
    private Timer transacaoTimer;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    public EfetuarTransacaoService(
            ContaPort contaPort,
            TransacaoStoragePort transacaoStoragePort,
            MeterRegistry meterRegistry,
            IdempotencyKeyRepository idempotencyKeyRepository) {
        this.contaPort = contaPort;
        this.transacaoStoragePort = transacaoStoragePort;
        this.meterRegistry = meterRegistry;
        this.idempotencyKeyRepository = idempotencyKeyRepository;
    }

    @PostConstruct
    public void initMetrics() {
        this.transacaoTimer = meterRegistry.timer("transacao.executada");
    }

    @Override
    @Transactional(rollbackFor = {OptimisticLockException.class, OptimisticLockingFailureException.class, Exception.class})
    public Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor, String idempotencyKey) {
        return transacaoTimer.record(() -> {

            if (idempotencyKeyRepository.existsById(idempotencyKey)) {
                log.warn("Transacao com chave {} ja foi processada. Ignorando nova tentativa.", idempotencyKey);
                throw new TransacaoDuplicadaException(idempotencyKey);
            }

            idempotencyKeyRepository.save(new IdempotencyKey(idempotencyKey));

            log.debug("Buscando conta origem: {}", idContaOrigem);
            Conta contaOrigem = contaPort.findById(idContaOrigem);
            Conta contaDestino = contaPort.findById(idContaDestino);

            if (contaOrigem == null) {
                throw new ContaNaoEncontradaException("Conta origem nao encontrada: " + idContaOrigem);
            }
            if (contaDestino == null) {
                throw new ContaNaoEncontradaException("Conta destino nao encontrada: " + idContaDestino);
            }

            if (!contaOrigem.podeEfetuarTransacao(valor)) {
                throw new SaldoInsuficienteException("Saldo/limite insuficiente para transacao de R$ " + valor);
            }

            try {
                contaOrigem.debitar(valor);
                contaDestino.creditar(valor);
                contaPort.update(contaOrigem);
                contaPort.update(contaDestino);
            } catch (OptimisticLockingFailureException | OptimisticLockException e) {
                log.warn("Concorrencia detectada durante atualizacao de contas: {}", e.getMessage());
                throw e;
            }

            Transacao transacao = new Transacao();
            transacao.setIdTransacao(UUID.randomUUID());
            transacao.setIdContaOrigem(idContaOrigem);
            transacao.setIdContaDestino(idContaDestino);
            transacao.setValor(valor);
            transacao.setDataHora(LocalDateTime.now());
            transacao.setStatus(TransacaoStatus.CONCLUIDA);

            transacaoStoragePort.save(transacao);

            log.info("Transacao {} concluida com sucesso", transacao.getIdTransacao());
            return transacao;
        });
    }
}

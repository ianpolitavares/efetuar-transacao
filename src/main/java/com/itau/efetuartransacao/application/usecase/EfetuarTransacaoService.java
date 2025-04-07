package com.itau.efetuartransacao.application.usecase;

import com.itau.efetuartransacao.application.ports.out.TransacaoStoragePort;
import com.itau.efetuartransacao.application.ports.out.ContaPort;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.domain.exception.SaldoInsuficienteException;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
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

    public EfetuarTransacaoService(
            ContaPort contaPort,
            TransacaoStoragePort transacaoStoragePort,
            MeterRegistry meterRegistry
    ) {
        this.contaPort = contaPort;
        this.transacaoStoragePort = transacaoStoragePort;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void initMetrics() {
        this.transacaoTimer = meterRegistry.timer("transacao.executada");
    }

    @Override
    public Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor) {
        return transacaoTimer.record(() -> {

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

            Transacao transacao = new Transacao();
            transacao.setIdTransacao(UUID.randomUUID().toString());
            transacao.setIdContaOrigem(idContaOrigem);
            transacao.setIdContaDestino(idContaDestino);
            transacao.setValor(valor);
            transacao.setDataHora(LocalDateTime.now());

            try {
                contaOrigem.debitar(valor);
                contaDestino.setSaldo(contaDestino.getSaldo() + valor);
                contaPort.update(contaOrigem);
                contaPort.update(contaDestino);
            } catch (Exception e) {
                log.error("Erro ao atualizar contas: {}", e.getMessage(), e);
                throw new RuntimeException("Erro na persistencia das contas");
            }

            transacao.setStatus(TransacaoStatus.CONCLUIDA);
            transacaoStoragePort.save(transacao);

            log.info("Transação {} concluida com sucesso", transacao.getIdTransacao());
            return transacao;
        });
    }
}

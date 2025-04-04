package com.itau.efetuartransacao.domain.service;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.infra.repository.TransacaoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class TransacaoService implements ITransacaoService {

    @Autowired
    private MeterRegistry meterRegistry;
    private Timer transacaoTimer;

    @PostConstruct
    public void initMetrics() {
        this.transacaoTimer = meterRegistry.timer("transacao.executada");
    }

    //@Autowired
    //private ContaRepository contaRepository;
    @Autowired
    private IContaProvider contaProvider;

    @Autowired
    private TransacaoRepository transacaoRepository;

    /**
     * Efetua a transação, retornando o objeto Transacao com status final.
     *
     * @param idContaOrigem  identificador da conta de Origem
     * @param idContaDestino identificador da conta de Destino
     * @param valor          valor da transação
     * @return Transacao (com status APROVADA ou RECUSADA)
     * @throws ContaNaoEncontradaException se a conta não existir
     * @throws SaldoInsuficienteException  se saldo + limite forem insuficientes
     */
    //@Transactional
    public Transacao efetuarTransacao(String idContaOrigem, String idContaDestino, Double valor) {
        // No cenário real, em que não estaria armazenando em memória, aqui seria implementado
        // o @Transaction para garantir a atomicidade

        return transacaoTimer.record(() -> {

            log.debug("Buscando conta origem: {}", idContaOrigem);
            Conta contaOrigem = contaProvider.findById(idContaOrigem);
            Conta contaDestino = contaProvider.findById(idContaDestino);

            if (contaOrigem == null) {
                throw new ContaNaoEncontradaException("Conta não encontrada para o idConta: " + idContaOrigem);
            }
            if (contaDestino == null) {
                throw new ContaNaoEncontradaException("Conta não encontrada para o idConta: " + idContaDestino);
            }

            // Validar se pode efetuar
            if (!contaOrigem.podeEfetuarTransacao(valor)) {
                throw new SaldoInsuficienteException("Saldo/limite insuficiente para efetuar transação de valor: " + valor);
            }

            // Criar a transação
            Transacao transacao = new Transacao();
            transacao.setIdTransacao(UUID.randomUUID().toString());
            transacao.setIdContaOrigem(idContaOrigem);
            transacao.setIdContaDestino(idContaDestino);
            transacao.setValor(valor);
            transacao.setDataHora(LocalDateTime.now());
            //transacao.setStatus(TransacaoStatus.PENDENTE);

            try {
                contaOrigem.debitar(valor);
                contaDestino.setSaldo(contaDestino.getSaldo() + valor);
                contaProvider.update(contaOrigem);
                contaProvider.update(contaDestino);

                //transacao.setStatus(TransacaoStatus.APROVADA);

            } catch (Exception e) {
                log.error("Erro ao atualizar contas: {}", e.getMessage(), e);
            }

            // Salvar a transação
            transacaoRepository.save(transacao);
            transacao.setStatus(TransacaoStatus.CONCLUIDA);

            log.info("Transacao concluida com status: {}", transacao.getStatus());
            return transacao;
        });
    }
}

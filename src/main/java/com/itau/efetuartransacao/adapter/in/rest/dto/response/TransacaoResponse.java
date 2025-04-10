package com.itau.efetuartransacao.adapter.in.rest.dto.response;

import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TransacaoResponse {
    private UUID idTransacao;
    private String idContaOrigem;
    private String idContaDestino;
    private Double valor;
    private LocalDateTime dataHora;
    private TransacaoStatus status;

    public TransacaoResponse(UUID id, String origem, String destino, Double valor, LocalDateTime dataHora, TransacaoStatus status) {
        this.idTransacao = id;
        this.idContaOrigem = origem;
        this.idContaDestino = destino;
        this.valor = valor;
        this.dataHora = dataHora;
        this.status = status;
    }

    public static TransacaoResponse fromDomain(com.itau.efetuartransacao.application.core.domain.model.Transacao transacao) {
        return new TransacaoResponse(
                transacao.getIdTransacao(),
                transacao.getIdContaOrigem(),
                transacao.getIdContaDestino(),
                transacao.getValor(),
                transacao.getDataHora(),
                transacao.getStatus()
        );
    }

}

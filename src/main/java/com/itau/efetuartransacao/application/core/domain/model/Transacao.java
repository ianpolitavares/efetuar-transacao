package com.itau.efetuartransacao.application.core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    private UUID idTransacao;
    private String idContaOrigem;
    private String idContaDestino;
    private Double valor;
    private LocalDateTime dataHora;
    private TransacaoStatus status;

    public Transacao(String idContaOrigem, String idContaDestino, Double valor) {
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
        this.valor = valor;
        this.dataHora = LocalDateTime.now();
        this.status = TransacaoStatus.PENDENTE;
    }
}

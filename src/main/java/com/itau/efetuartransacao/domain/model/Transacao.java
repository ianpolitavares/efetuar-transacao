package com.itau.efetuartransacao.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Classe que representa a transação financeira.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transacao {
    private String idTransacao;
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

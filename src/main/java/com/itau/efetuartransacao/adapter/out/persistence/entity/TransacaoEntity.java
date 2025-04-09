package com.itau.efetuartransacao.adapter.out.persistence.entity;

import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransacaoEntity {

    @Id
    private String idTransacao;

    private String idContaOrigem;
    private String idContaDestino;
    private Double valor;

    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    private TransacaoStatus status;
}

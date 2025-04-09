package com.itau.efetuartransacao.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "contas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaEntity {

    @Id
    private String idConta;

    private Double saldo;
    private Double limite;
}

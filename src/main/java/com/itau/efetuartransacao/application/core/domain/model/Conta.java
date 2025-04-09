package com.itau.efetuartransacao.application.core.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "contas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    @Id
    private String idConta;

    private Double saldo;

    private Double limite;

    private Double limiteUtilizado;

    /**
     * Verifica se a conta pode efetuar uma transação considerando o saldo + limite disponível.
     */
    public boolean podeEfetuarTransacao(Double valor) {
        double limiteDisponivel = limite - limiteUtilizado;
        return valor <= (saldo + limiteDisponivel);
    }

    /**
     * Debita o valor da conta, usando saldo e depois o limite, se necessário.
     */
    public void debitar(Double valor) {
        if (valor <= saldo) {
            saldo -= valor;
        } else {
            double restante = valor - saldo;
            saldo = 0.0;
            limiteUtilizado += restante;
        }
    }

    /**
     * Credita o valor na conta. Primeiro, quita o limite utilizado; o restante vai para o saldo.
     */
    public void creditar(Double valor) {
        if (limiteUtilizado > 0) {
            double quitado = Math.min(valor, limiteUtilizado);
            limiteUtilizado -= quitado;
            valor -= quitado;
        }
        saldo += valor;
    }
}

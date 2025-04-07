package com.itau.efetuartransacao.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa a conta do cliente.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Conta {

    private String idConta;
    private Double saldo;
    private Double limite;

    /**
     * Verifica se a conta possui saldo e/ou limite suficiente
     * para efetuar a transação.
     *
     * @param valor Valor da transação
     * @return true se é possível efetuar a transação; false caso contrário
     */
    public boolean podeEfetuarTransacao(Double valor) {
        return (valor <= (this.saldo + this.limite));
    }

    /**
     * Debita o valor do saldo, levando em conta o limite
     * caso o saldo não seja suficiente.
     *
     * @param valor Valor a ser debitado
     */
    public void debitar(Double valor) {
        if (valor <= this.saldo) {
            this.saldo -= valor;
        } else {
            // Usa parte do saldo e parte do limite
            double resto = valor - this.saldo;
            this.saldo = 0.0;
            this.limite -= resto;

        }
    }
}

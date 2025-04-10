package com.itau.efetuartransacao.adapter.in.rest.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;



/**
 * DTO para requisição de transação.
 */
@Valid
@Data
public class TransacaoRequest {

    @NotBlank(message = "O id da conta Origem é obrigatório.")
    private String idContaOrigem;

    @NotBlank(message = "O id da conta Destino e obrigatorio.")
    private String idContaDestino;

    @NotNull(message = "O valor da transacao nao pode ser nulo.")
    @Positive(message = "O valor da transacao deve ser maior que zero.")
    private Double valor;
}

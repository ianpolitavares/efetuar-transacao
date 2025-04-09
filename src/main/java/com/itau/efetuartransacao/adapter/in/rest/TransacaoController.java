package com.itau.efetuartransacao.adapter.in.rest;

import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import com.itau.efetuartransacao.adapter.in.rest.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller responsável por lidar com requisições relacionadas a transações.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/transacoes")
public class TransacaoController {

    @Autowired
    private EfetuarTransacaoUseCase transacaoUseCase;

    /**
     * Endpoint para efetuar uma transação.
     *
     * @param transacaoRequest Objeto contendo idContaOrigem, idContaDestino e valor
     * @return Detalhes da transação realizada (ou erro, em caso de falha)
     */
    @PostMapping
    public ResponseEntity<Transacao> efetuarTransacao(@Valid @RequestBody TransacaoRequest transacaoRequest) {

        log.info("Iniciando transacao de {} -> {} | valor: {}",
                transacaoRequest.getIdContaOrigem(),
                transacaoRequest.getIdContaDestino(),
                transacaoRequest.getValor());

        Transacao transacao = transacaoUseCase.efetuarTransacao(
                transacaoRequest.getIdContaOrigem(),
                transacaoRequest.getIdContaDestino(),
                transacaoRequest.getValor()
        );

        log.info("Transacao finalizada: {}", transacao);
        //return ResponseEntity.status(HttpStatus.OK).body(transacao);
        return ResponseEntity.ok(transacao);
    }
}

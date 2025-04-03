package com.itau.efetuartransacao.application.controller;

import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.service.TransacaoService;
import com.itau.efetuartransacao.application.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por lidar com requisições relacionadas a transações.
 */
@RestController
@RequestMapping("/api/v1/transacoes")
public class TransacaoController {

    @Autowired
    private TransacaoService transacaoService;

    /**
     * Endpoint para efetuar uma transação.
     *
     * @param transacaoRequest Objeto contendo idContaOrigem, idContaDestino e valor
     * @return Detalhes da transação realizada (ou erro, em caso de falha)
     */
    @PostMapping
    public ResponseEntity<Transacao> efetuarTransacao(@Valid @RequestBody TransacaoRequest transacaoRequest) {
        Transacao transacao = transacaoService.efetuarTransacao(
                transacaoRequest.getIdContaOrigem(),
                transacaoRequest.getIdContaDestino(),
                transacaoRequest.getValor()
        );
        //return ResponseEntity.status(HttpStatus.OK).body(transacao);
        return ResponseEntity.ok(transacao);
    }
}

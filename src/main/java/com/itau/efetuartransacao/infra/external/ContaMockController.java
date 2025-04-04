package com.itau.efetuartransacao.infra.external;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.service.IContaProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller mockado para simular a API externa de contas.
 */
@Slf4j
@RestController
@RequestMapping("/mock/api/contas")
public class ContaMockController {

    //@Autowired
    //private ContaRepository contaRepository;
    @Autowired
    private IContaProvider contaProvider;

    /**
     * Retorna os dados da conta (saldo e limite) com base no idConta.
     *
     * @param idConta identificador da conta
     * @return dados da conta ou 404 se não encontrada
     */
    @GetMapping("/{idConta}")
    public ResponseEntity<Conta> getConta(@PathVariable String idConta) {
        log.debug("Mock recebendo requisição para conta: {}", idConta);
        Conta conta = contaProvider.findById(idConta);
        if (conta == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(conta);
    }
}

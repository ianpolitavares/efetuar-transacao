package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.domain.model.Conta;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * Mudei a implementação para o "InMemoryContaProvider para melhorar a abstração
 * Antes, a TransacaoService dependia do ContaRepository
 * Agora o
 * */
@Repository
public class ContaRepository {

    // Armazena as contas em memória, chave: idConta
    private final Map<String, Conta> contasMap = new HashMap<>();

    /**
     * Método para inicializar alguns dados de contas para teste.
     */
    @PostConstruct
    public void init() {
        // Contas para teste
        // Aqui poderia ser feito a consulta da conta em uma outra API
        // Ou mesmo no banco de dados, mas para fins de mock, está sendo instanciado duas contas
        Conta conta1 = new Conta("12345-6", 1000.0, 500.0);
        Conta conta2 = new Conta("98765-4", 2000.0, 1000.0);

        contasMap.put(conta1.getIdConta(), conta1);
        contasMap.put(conta2.getIdConta(), conta2);
    }

    /**
     * Retorna a conta correspondente ao idConta, ou null se não existir.
     */
    public Conta findContaById(String idConta) {
        return contasMap.get(idConta);
    }

    /**
     * Atualiza a conta no repositório (após transação).
     */
    public void updateConta(Conta conta) {
        contasMap.put(conta.getIdConta(), conta);
    }

}

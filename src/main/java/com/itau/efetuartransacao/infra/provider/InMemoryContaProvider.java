package com.itau.efetuartransacao.infra.provider;

import com.itau.efetuartransacao.domain.model.Conta;
import com.itau.efetuartransacao.domain.service.ContaProvider;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryContaProvider implements ContaProvider {

    private final Map<String, Conta> contasMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Conta conta1 = new Conta("12345-6", 1000.0, 500.0);
        Conta conta2 = new Conta("98765-4", 2000.0, 1000.0);

        contasMap.put(conta1.getIdConta(), conta1);
        contasMap.put(conta2.getIdConta(), conta2);
    }

    @Override
    public Conta findById(String idConta) {
        return contasMap.get(idConta);
    }

    @Override
    public void update(Conta conta) {
        contasMap.put(conta.getIdConta(), conta);
    }
}

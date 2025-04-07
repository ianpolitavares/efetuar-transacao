package com.itau.efetuartransacao.application.ports.out;

import com.itau.efetuartransacao.domain.model.Conta;

public interface ContaPort {
    Conta findById(String idConta);
    void update(Conta conta);
}

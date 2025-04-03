package com.itau.efetuartransacao.domain.service;

import com.itau.efetuartransacao.domain.model.Conta;

public interface IContaProvider {
    Conta findById(String idConta);
    void update(Conta conta);
}

package com.itau.efetuartransacao.adapter.out.mock;

import com.itau.efetuartransacao.adapter.out.fake.ContaPortImpl;
import com.itau.efetuartransacao.adapter.out.persistence.ContaRepositoryAdapter;
import com.itau.efetuartransacao.domain.model.Conta;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/mock/api/contas")
@RequiredArgsConstructor
public class ContaMockRestController {

    private final ContaRepositoryAdapter contaRepositoryAdapter;

    @GetMapping("/{id}")
    public Conta get(@PathVariable String id) {
        return contaRepositoryAdapter.findById(id);
    }
}

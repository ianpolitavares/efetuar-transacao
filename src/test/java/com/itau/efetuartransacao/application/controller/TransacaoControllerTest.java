package com.itau.efetuartransacao.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.efetuartransacao.application.dto.TransacaoRequest;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import com.itau.efetuartransacao.domain.service.ITransacaoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
public class TransacaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransacaoService transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testEfetuarTransacao_retorna200() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(150.0);

        Transacao mockResponse = new Transacao();
        mockResponse.setIdTransacao(UUID.randomUUID().toString());
        mockResponse.setIdContaOrigem("1");
        mockResponse.setIdContaDestino("2");
        mockResponse.setValor(150.0);
        mockResponse.setStatus(TransacaoStatus.CONCLUIDA);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idContaOrigem").value("1"))
                .andExpect(jsonPath("$.idContaDestino").value("2"))
                .andExpect(jsonPath("$.valor").value(150.0))
                .andExpect(jsonPath("$.status").value("CONCLUIDA"));
    }
}
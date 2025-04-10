package com.itau.efetuartransacao.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.efetuartransacao.adapter.in.rest.dto.TransacaoRequest;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import com.itau.efetuartransacao.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.domain.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
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
    private EfetuarTransacaoUseCase transacaoService;

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

    @Test
    public void testEfetuarTransacao_camposInvalidos_retorna400() throws Exception {
        TransacaoRequest request = new TransacaoRequest(); // todos os campos nulos

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.idContaOrigem").exists())
                .andExpect(jsonPath("$.idContaDestino").exists())
                .andExpect(jsonPath("$.valor").exists());
    }

    @Test
    public void testEfetuarTransacao_contaNaoEncontrada_retorna404() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new ContaNaoEncontradaException("Conta não encontrada"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Conta não encontrada"));
    }

    @Test
    public void testEfetuarTransacao_saldoInsuficiente_retorna422() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(9999.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new SaldoInsuficienteException("Saldo insuficiente"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Saldo insuficiente"));
    }

    @Test
    public void testEfetuarTransacao_erroInterno_retorna500() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro inesperado: Erro genérico"));
    }
}

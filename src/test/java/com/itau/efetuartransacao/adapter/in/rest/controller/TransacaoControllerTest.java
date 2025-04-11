package com.itau.efetuartransacao.adapter.in.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.efetuartransacao.adapter.in.rest.dto.request.TransacaoRequest;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import com.itau.efetuartransacao.application.core.domain.exception.ContaNaoEncontradaException;
import com.itau.efetuartransacao.application.core.domain.exception.SaldoInsuficienteException;
import com.itau.efetuartransacao.application.core.domain.model.Transacao;
import com.itau.efetuartransacao.application.core.domain.model.TransacaoStatus;
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
        mockResponse.setIdTransacao(UUID.randomUUID());
        mockResponse.setIdContaOrigem("1");
        mockResponse.setIdContaDestino("2");
        mockResponse.setValor(150.0);
        mockResponse.setStatus(TransacaoStatus.CONCLUIDA);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/transacoes")
                        .header("Idempotency-Key", "teste-123")
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
                        .header("Idempotency-Key", "teste-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void testEfetuarTransacao_contaNaoEncontrada_retorna404() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new ContaNaoEncontradaException("Conta nao encontrada"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .header("Idempotency-Key", "teste-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Conta nao encontrada"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void testEfetuarTransacao_saldoInsuficiente_retorna422() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(9999.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new SaldoInsuficienteException("Saldo insuficiente"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .header("Idempotency-Key", "teste-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void testEfetuarTransacao_erroInterno_retorna500() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Erro genérico"));

        mockMvc.perform(post("/api/v1/transacoes")
                        .header("Idempotency-Key", "teste-123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Erro inesperado: Erro genérico"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    void deveRetornar400QuandoIdempotencyKeyNaoInformado() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Header obrigatorio nao informado: Idempotency-Key"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }
}

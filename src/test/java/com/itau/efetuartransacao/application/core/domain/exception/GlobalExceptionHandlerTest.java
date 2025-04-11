package com.itau.efetuartransacao.application.core.domain.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.efetuartransacao.adapter.in.rest.controller.TransacaoController;
import com.itau.efetuartransacao.adapter.in.rest.dto.request.TransacaoRequest;
import com.itau.efetuartransacao.application.ports.in.EfetuarTransacaoUseCase;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransacaoController.class)
public class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EfetuarTransacaoUseCase transacaoService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testContaNaoEncontradaException() throws Exception {
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new ContaNaoEncontradaException("Conta nao encontrada"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("999");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", "abc-123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Conta nao encontrada"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void testSaldoInsuficienteException() throws Exception {
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new SaldoInsuficienteException("Saldo insuficiente"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(999.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", "abc-123"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.message").value("Saldo insuficiente"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void testErroInternoGenerico() throws Exception {
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(150.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", "abc-123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("Erro inesperado: Erro inesperado"))
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }

    @Test
    public void deveRetornar400QuandoDadosInvalidos() throws Exception {
        TransacaoRequest request = new TransacaoRequest(); // todos os campos nulos

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Idempotency-Key", "abc-123"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.path").value("/api/v1/transacoes"));
    }
}

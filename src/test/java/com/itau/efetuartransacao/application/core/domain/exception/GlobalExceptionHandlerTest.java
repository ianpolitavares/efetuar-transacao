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
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new ContaNaoEncontradaException("Conta nao encontrada"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("999");
        request.setIdContaDestino("2");
        request.setValor(100.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Conta nao encontrada"));
    }

    @Test
    public void testSaldoInsuficienteException() throws Exception {
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new SaldoInsuficienteException("Saldo insuficiente"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(999.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().string("Saldo insuficiente"));
    }

    @Test
    public void testErroInternoGenerico() throws Exception {
        Mockito.when(transacaoService.efetuarTransacao(any(), any(), any()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        TransacaoRequest request = new TransacaoRequest();
        request.setIdContaOrigem("1");
        request.setIdContaDestino("2");
        request.setValor(150.0);

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro inesperado: Erro inesperado"));
    }

    @Test
    public void deveRetornar400QuandoDadosInvalidos() throws Exception {
        TransacaoRequest request = new TransacaoRequest();
        // todos os campos nulos → viola todas as validações

        mockMvc.perform(post("/api/v1/transacoes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.idContaOrigem").exists())
                .andExpect(jsonPath("$.idContaDestino").exists())
                .andExpect(jsonPath("$.valor").exists());
    }
}

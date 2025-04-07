package com.itau.efetuartransacao.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itau.efetuartransacao.adapter.in.rest.dto.TransacaoRequest;
import com.itau.efetuartransacao.domain.model.Transacao;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransacaoControllerConcurrencyTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void testConcurrentRequests() throws Exception {
        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threads);

        List<Future<ResponseEntity<Transacao>>> responses = new ArrayList<>();

        for (int i = 0; i < threads; i++) {
            int finalI = i;
            Callable<ResponseEntity<Transacao>> task = () -> {
                try {
                    TransacaoRequest req = new TransacaoRequest();
                    req.setIdContaOrigem("12345-6");
                    req.setIdContaDestino("98765-4");
                    req.setValor(10.0 + finalI); // valores diferentes

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(req), headers);

                    String url = "http://localhost:" + port + "/api/v1/transacoes";
                    return restTemplate.exchange(url, HttpMethod.POST, entity, Transacao.class);
                } finally {
                    latch.countDown();
                }
            };

            responses.add(executor.submit(task));
        }

        latch.await(); // aguarda todas as threads
        executor.shutdown();

        for (Future<ResponseEntity<Transacao>> future : responses) {
            ResponseEntity<Transacao> response = future.get();
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals("12345-6", response.getBody().getIdContaOrigem());
        }
    }
}

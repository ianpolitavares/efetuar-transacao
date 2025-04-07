package com.itau.efetuartransacao.adapter.out.persistence;

import com.itau.efetuartransacao.domain.model.Transacao;
import com.itau.efetuartransacao.domain.model.TransacaoStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class TransacaoRepositoryConcurrencyTest {

    @Test
    public void testConcurrentSaves() throws InterruptedException {
        TransacaoRepository repository = new TransacaoRepository();

        int numThreads = 50;
        CountDownLatch latch = new CountDownLatch(numThreads);
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = 0; i < numThreads; i++) {
            int finalI = i;
            executor.submit(() -> {
                try {
                    String id = "t" + finalI;
                    Transacao transacao = new Transacao();
                    transacao.setIdTransacao(id);
                    transacao.setIdContaOrigem("1");
                    transacao.setIdContaDestino("2");
                    transacao.setValor((double) finalI);
                    transacao.setStatus(TransacaoStatus.CONCLUIDA);
                    transacao.setDataHora(LocalDateTime.now());

                    repository.save(transacao);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        // verifica se todos os registros foram salvos
        for (int i = 0; i < numThreads; i++) {
            String id = "t" + i;
            Transacao encontrada = repository.findById(id);
            assertNotNull(encontrada, "Transação " + id + " deveria estar presente");
            assertEquals((double) i, encontrada.getValor());
        }
    }
}

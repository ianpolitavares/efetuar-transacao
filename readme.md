# Efetuar Transação

Sistema backend RESTful para efetuação de transações financeiras entre contas, com arquitetura hexagonal, Resilience4j, testes unitários, mutantes, concorrentes e integração com banco de dados PostgreSQL via Docker.

---

## Visão Geral

Este projeto simula uma aplicação de transação bancária, contemplando:

- API REST para efetuar transações
- Controle de saldo e limite com controle de limite utilizado
- Resiliência com Resilience4j (retry + circuit breaker)
- Observabilidade com Micrometer
- Arquitetura baseada em portas e adaptadores (hexagonal)
- Validação robusta de dados com Bean Validation
- Testes unitários com JUnit + Mockito
- Testes mutantes com PITest
- Testes concorrentes com simulação de carga
- Integração com banco PostgreSQL e versionamento de schema com Flyway
- Inicialização de ambiente com Docker

---

## Tecnologias e Bibliotecas

- Java 17
- Spring Boot 3.0.4
- Spring Web / AOP / Validation / Data JPA
- PostgreSQL
- Micrometer
- Resilience4j
- Flyway
- Docker / Docker Compose
- JUnit 5 / Mockito / PITest

---

## Arquitetura Hexagonal

### Core (domínio)
- `domain.model`: entidades `Conta` e `Transacao`
- `domain.exception`: exceções de regra de negócio

### Application
- `application.usecase`: regra de negócio principal (`EfetuarTransacaoService`)
- `application.ports.in`: entrada (interface `EfetuarTransacaoUseCase`)
- `application.ports.out`: saída (interfaces `ContaPort`, `TransacaoStoragePort`)

### Adapters
- `adapter.in.rest`: controlador REST (`TransacaoController`)
- `adapter.out.persistence`: repositório de transações e contas com Spring Data JPA

### Observabilidade
- Micrometer + Timer para medir tempo da transação

### Resiliência
- Retry e circuit breaker nos acessos a dados

---

## Como executar

### Usando Maven

```bash
# 1. Compilar o projeto
mvn clean install

# 2. Executar a aplicação
mvn spring-boot:run
```

### Usando Docker

```bash
# Subir banco PostgreSQL
docker-compose up -d
```

Certifique-se de que o banco esteja acessível em:
```
jdbc:postgresql://localhost:5432/banco
user: 
senha: 
```

A aplicação inicia em:
```
http://localhost:8080/api/v1/transacoes
```

---

## Como testar

### Testes unitários
```bash
mvn test
```

### Testes mutantes
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```
Gera relatório HTML em `target/pit-reports`

### Testes concorrentes
Executados com `TransacaoControllerConcurrencyTest`, simulando concorrência com `ExecutorService`.

---

## Fluxograma da Transação

```mermaid
sequenceDiagram
    participant Cliente
    participant TransacaoController
    participant EfetuarTransacaoService
    participant IdempotencyKeyRepository
    participant ContaPort
    participant TransacaoStoragePort

    Cliente->>TransacaoController: POST /api/v1/transacoes
    TransacaoController->>EfetuarTransacaoService: efetuarTransacao(idOrigem, idDestino, valor, idempotency-key)

    EfetuarTransacaoService->>IdempotencyKeyRepository: existsById(idempotency-key)
    alt Chave já existe
        EfetuarTransacaoService-->>TransacaoController: throw TransacaoDuplicadaException
        TransacaoController-->>Cliente: 409 Conflict
    else Nova transação
        EfetuarTransacaoService->>IdempotencyKeyRepository: save(idempotency-key)

        EfetuarTransacaoService->>ContaPort: findById(idOrigem)
        ContaPort-->>EfetuarTransacaoService: Conta origem

        EfetuarTransacaoService->>ContaPort: findById(idDestino)
        ContaPort-->>EfetuarTransacaoService: Conta destino

        alt Conta não encontrada
            EfetuarTransacaoService-->>TransacaoController: throw ContaNaoEncontradaException
            TransacaoController-->>Cliente: 404 Not Found
        else Saldo insuficiente
            EfetuarTransacaoService-->>TransacaoController: throw SaldoInsuficienteException
            TransacaoController-->>Cliente: 400 Bad Request
        else Sucesso
            EfetuarTransacaoService->>Conta: debitar(valor)
            EfetuarTransacaoService->>Conta: creditar(valor)

            EfetuarTransacaoService->>ContaPort: update(contaOrigem)
            EfetuarTransacaoService->>ContaPort: update(contaDestino)

            EfetuarTransacaoService->>TransacaoStoragePort: save(transacao)
            alt Concorrência detectada
                TransacaoStoragePort-->>EfetuarTransacaoService: throw OptimisticLockException
                EfetuarTransacaoService-->>TransacaoController: erro de concorrência
                TransacaoController-->>Cliente: 409 Conflict
            else Sucesso
                EfetuarTransacaoService-->>TransacaoController: Transacao (status CONCLUIDA)
                TransacaoController-->>Cliente: 200 OK + TransacaoResponse
            end
        end
    end
```

---

## Exemplo de requisição

POST `/api/v1/transacoes`

```json
{
  "idContaOrigem": "12345-6",
  "idContaDestino": "98765-4",
  "valor": 150.00
}
```

Resposta:

```json
{
  "idTransacao": "...",
  "idContaOrigem": "12345-6",
  "idContaDestino": "98765-4",
  "valor": 150.0,
  "status": "CONCLUIDA",
  "dataHora": "2025-04-07T18:00:00"
}
```

---

## Observabilidade

- Métrica `transacao.executada` com Micrometer
- Exportável via Actuator ou Prometheus

---

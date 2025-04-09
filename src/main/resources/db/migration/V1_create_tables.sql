-- tabela de contas
CREATE TABLE IF NOT EXISTS contas (
    id_conta VARCHAR(255) PRIMARY KEY,
    saldo NUMERIC(15, 2) NOT NULL,
    limite NUMERIC(15, 2) NOT NULL,
    limite_utilizado NUMERIC(15, 2) NOT NULL
);

-- tabela de transações
CREATE TABLE IF NOT EXISTS transacoes (
    id_transacao UUID PRIMARY KEY,
    id_conta_origem VARCHAR(255) NOT NULL,
    id_conta_destino VARCHAR(255) NOT NULL,
    valor NUMERIC(15, 2) NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL
);

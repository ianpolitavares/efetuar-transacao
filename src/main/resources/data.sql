INSERT INTO contas (id_conta, saldo, limite)
VALUES ('12345-6', 1000.00, 500.00)
ON CONFLICT (id_conta) DO NOTHING;

INSERT INTO contas (id_conta, saldo, limite)
VALUES ('98765-4', 2000.00, 1000.00)
ON CONFLICT (id_conta) DO NOTHING;

INSERT INTO contas (id_conta, saldo, limite, limite_utilizado)
VALUES ('12345-6', 1000.00, 500.00, 0.0)
ON CONFLICT (id_conta) DO NOTHING;

INSERT INTO contas (id_conta, saldo, limite, limite_utilizado)
VALUES ('98765-4', 2000.00, 1000.00, 0.0)
ON CONFLICT (id_conta) DO NOTHING;

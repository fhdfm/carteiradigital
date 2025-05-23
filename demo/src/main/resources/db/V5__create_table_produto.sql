-- TABELA PRODUTO
-- ==========================
CREATE TABLE produto (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    escola_id INT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    foto VARCHAR(255),
    preco DECIMAL(10,2) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    quantidade_vendida BIGINT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW()

    CONSTRAINT fk_produto_escola FOREIGN KEY (escola_id) REFERENCES escola(id) ON DELETE CASCADE,
    CONSTRAINT uq_produto_uuid UNIQUE (uuid)
);

CREATE INDEX idx_produto_id ON produto(id);
CREATE INDEX idx_produto_uuid ON produto(uuid);
CREATE INDEX idx_produto_escola ON produto(escola_id);
CREATE INDEX idx_produto_departamento ON produto(departamento);
CREATE INDEX idx_produto_escola_departamento ON produto(escola_id, departamento);

-- ==========================

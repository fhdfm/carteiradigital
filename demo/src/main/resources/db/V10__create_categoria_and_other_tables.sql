-- TABELA CATEGORIA
-- ==========================
CREATE TABLE categoria_produto
(
    id        SERIAL PRIMARY KEY,
    uuid      UUID UNIQUE           DEFAULT uuid_generate_v4(),
    nome      VARCHAR(255) NOT NULL UNIQUE,
    status    VARCHAR(50)  NOT NULL,
    escola_id INT          NOT NULL REFERENCES escola (id),
    version   INT          NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW(),
);



-- ==========================
-- ÍNDICES INDIVIDUAIS
-- ==========================

-- Índice para busca rápida pelo pedido
CREATE INDEX idx_item_pedido_pedido_id ON item_pedido(pedido_id);

CREATE TABLE escola_endereco (
    id BIGSERIAL PRIMARY KEY,
    escola_id BIGINT NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    endereco VARCHAR(100) NOT NULL,
    numero VARCHAR(10) NOT NULL,
    bairro VARCHAR(50) NOT NULL,
    complemento VARCHAR(50) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_escola_info_escola
        FOREIGN KEY (escola_id)
        REFERENCES escola(id)
        ON DELETE CASCADE
);

CREATE TABLE escola_financeiro (
    id SERIAL PRIMARY KEY,
    escola_id INT NOT NULL UNIQUE REFERENCES escola(id) ON DELETE CASCADE,
    dia_pagamento INT NOT NULL,
    dia_recebimento INT NOT NULL,
    version INT NOT NULL DEFAULT 0
);

CREATE TABLE revinfo (
    id SERIAL PRIMARY KEY,
    timestamp BIGINT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    usuario_id BIGINT,
    escola_id BIGINT
);

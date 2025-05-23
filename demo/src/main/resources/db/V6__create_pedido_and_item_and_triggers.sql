-- TABELA PEDIDO (ATUALIZADA)
-- ==========================
CREATE TABLE pedido (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    escola_id INT NOT NULL REFERENCES escola(id),
    comprador_id BIGINT REFERENCES aluno(id),
    vendedor_id INT NOT NULL REFERENCES usuario(id),
    valor_total NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) CHECK (status IN ('ABERTO', 'CONCLUIDO', 'CANCELADO')) DEFAULT 'ABERTO' NOT NULL,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW(),
    version INT NOT NULL DEFAULT 0
);

-- ==========================
-- ÍNDICES INDIVIDUAIS
-- ==========================
CREATE INDEX idx_pedido_uuid ON pedido (uuid);
CREATE INDEX idx_pedido_escola_id ON pedido (escola_id);
CREATE INDEX idx_pedido_comprador_id ON pedido (comprador_id);
CREATE INDEX idx_pedido_vendedor_id ON pedido (vendedor_id);

-- ==========================
-- ÍNDICES COMPOSTOS
-- ==========================
CREATE INDEX idx_pedido_escola_criado_em ON pedido (escola_id, criado_em);
CREATE INDEX idx_pedido_comprador_criado_em ON pedido (comprador_id, criado_em);
CREATE INDEX idx_pedido_vendedor_criado_em ON pedido (vendedor_id, criado_em);
CREATE INDEX idx_pedido_escola_status_criado_em ON pedido (escola_id, status, criado_em);

-- ==========================
-- TABELA PEDIDO_ITEM
-- ==========================
CREATE TABLE item_pedido (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NULL, -- Produto opcional
    descricao_produto VARCHAR(255) NOT NULL, -- Descrição independente do produto
    quantidade INT NOT NULL,
    valor_unitario NUMERIC(15,2) NOT NULL,
    valor_total NUMERIC(15,2) GENERATED ALWAYS AS (quantidade * valor_unitario) STORED,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW(),

    -- Chave estrangeira com o pedido
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,

    -- Chave estrangeira opcional com o produto
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE SET NULL
);

-- ==========================
-- TRIGGER PEDIDO
-- ==========================

CREATE OR REPLACE FUNCTION trg_pedido_total()
RETURNS trigger
LANGUAGE plpgsql AS
$$
DECLARE
v_pedido_id bigint;
BEGIN
  v_pedido_id := COALESCE(NEW.pedido_id, OLD.pedido_id);

UPDATE pedido
SET valor_total = COALESCE(
        (SELECT SUM(valor_total)
         FROM item_pedido
         WHERE pedido_id = v_pedido_id), 0)
WHERE id = v_pedido_id;

RETURN NULL;
END;
$$;

CREATE TRIGGER aiud_item_pedido_total
    AFTER INSERT OR UPDATE OR DELETE ON item_pedido
    FOR EACH ROW EXECUTE FUNCTION trg_pedido_total();


-- ==========================

-- TABELA TRANSAÇÃO
-- ==========================
CREATE TABLE transacao (
                           id BIGSERIAL PRIMARY KEY,
                           uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
                           carteira_id BIGINT NOT NULL,
                           valor DECIMAL(19, 2) NOT NULL DEFAULT 0,
                           tipo_transacao VARCHAR NOT NULL,
                           usuario_id BIGINT,
                           pedido_id BIGINT,
                           version INT NOT NULL DEFAULT 0,
                           criado_em TIMESTAMP DEFAULT NOW(),
                           atualizado_em TIMESTAMP DEFAULT NOW(),
                           CONSTRAINT fk_transacao_carteira
                               FOREIGN KEY (carteira_id) REFERENCES carteira(id)
                                   ON DELETE NO ACTION,
                           CONSTRAINT fk_transacao_usuario
                               FOREIGN KEY (usuario_id) REFERENCES usuario(id)
                                   ON DELETE NO ACTION,
                           CONSTRAINT fk_transacao_pedido
                               FOREIGN KEY (pedido_id) REFERENCES pedido(id)
                                   ON DELETE NO ACTION,
                           CONSTRAINT uq_transacao_uuid UNIQUE (uuid)
);

-- Índices (além dos UNIQUE, que geram índices implícitos,
-- podemos criar índices adicionais para buscas específicas)
CREATE INDEX idx_transacao_id        ON transacao(id);
CREATE INDEX idx_transacao_uuid      ON transacao(uuid);
CREATE INDEX idx_transacao_carteira     ON transacao(carteira_id);


-- ==========================
-- TRIGGER CARTEIRA TRANSAÇÃO
-- ==========================
CREATE OR REPLACE FUNCTION atualizar_saldo_carteira()
RETURNS TRIGGER AS $$
BEGIN
  -- INSERT
  IF TG_OP = 'INSERT' THEN
    IF NEW.tipo_transacao = 'CREDITO' THEN
UPDATE carteira SET saldo = saldo + NEW.valor WHERE id = NEW.carteira_id;
ELSIF NEW.tipo_transacao = 'DEBITO' THEN
UPDATE carteira SET saldo = saldo - NEW.valor WHERE id = NEW.carteira_id;
END IF;

  -- UPDATE
  ELSIF TG_OP = 'UPDATE' THEN
    IF OLD.tipo_transacao = 'CREDITO' THEN
UPDATE carteira SET saldo = saldo - OLD.valor WHERE id = OLD.carteira_id;
ELSIF OLD.tipo_transacao = 'DEBITO' THEN
UPDATE carteira SET saldo = saldo + OLD.valor WHERE id = OLD.carteira_id;
END IF;

    IF NEW.tipo_transacao = 'CREDITO' THEN
UPDATE carteira SET saldo = saldo + NEW.valor WHERE id = NEW.carteira_id;
ELSIF NEW.tipo_transacao = 'DEBITO' THEN
UPDATE carteira SET saldo = saldo - NEW.valor WHERE id = NEW.carteira_id;
END IF;
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_atualizar_saldo_carteira
    AFTER INSERT OR UPDATE ON transacao
                        FOR EACH ROW
                        EXECUTE FUNCTION atualizar_saldo_carteira();


-- ==========================

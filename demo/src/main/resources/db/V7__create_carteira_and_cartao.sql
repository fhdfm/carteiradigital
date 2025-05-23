-- TABELA Carteira
-- ==========================
CREATE TABLE carteira (
                          id BIGSERIAL PRIMARY KEY,
                          uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
                          aluno_id BIGINT NOT NULL,
                          saldo DECIMAL(19, 2) NOT NULL DEFAULT 0,
                          version INT NOT NULL DEFAULT 0,
                          criado_em TIMESTAMP DEFAULT NOW(),
                          atualizado_em TIMESTAMP DEFAULT NOW(),
                          CONSTRAINT fk_carteira_aluno
                              FOREIGN KEY (aluno_id) REFERENCES aluno(id)
                                  ON DELETE NO ACTION,
                          CONSTRAINT uq_carteira_uuid UNIQUE (uuid)
);

-- Índices (além dos UNIQUE, que geram índices implícitos,
-- podemos criar índices adicionais para buscas específicas)
CREATE INDEX idx_carteira_id        ON carteira(id);
CREATE INDEX idx_carteira_uuid      ON carteira(uuid);
CREATE INDEX idx_carteira_aluno     ON carteira(aluno_id);


-- ==========================


-- TABELA CARTÃO_CARTEIRA
-- ==========================
CREATE TABLE cartao_carteira (
                                 id BIGSERIAL PRIMARY KEY,
                                 uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
                                 carteira_id BIGINT NOT NULL,
                                 numero VARCHAR(255) NOT NULL,
                                 senha VARCHAR(255) NOT NULL,
                                 status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO' NOT NULL,
                                 version INT NOT NULL DEFAULT 0,
                                 criado_em TIMESTAMP DEFAULT NOW(),
                                 atualizado_em TIMESTAMP DEFAULT NOW(),

    -- Chave estrangeira para a tabela aluno
                                 CONSTRAINT fk_cartao_carteira
                                     FOREIGN KEY (carteira_id) REFERENCES carteira(id) ON DELETE NO ACTION,

    -- Constraint de unicidade para o campo uuid
                                 CONSTRAINT uq_cartao_carteira_uuid UNIQUE (uuid)
);

-- Índices (além dos UNIQUE, que geram índices implícitos,
-- podemos criar índices adicionais para buscas específicas)
CREATE INDEX idx_cartao_carteira_id        ON cartao_carteira(id);
CREATE INDEX idx_cartao_carteira_uuid      ON cartao_carteira(uuid);
CREATE INDEX idx_cartao_carteira_carteira  ON cartao_carteira(carteira_id);
CREATE INDEX idx_cartao_carteira_numero    ON cartao_carteira (numero);


-- ==========================

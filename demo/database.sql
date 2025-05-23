-- ==========================
-- EXTENSÃO UUID
-- ==========================
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================
-- TABELA ESCOLA
-- ==========================
CREATE TABLE escola (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO',
    payment_secret VARCHAR(255) UNIQUE,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_escola_uuid ON escola(uuid);

-- ==========================
-- TABELA USUÁRIO
-- ==========================
CREATE TABLE usuario (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
    escola_id INT REFERENCES escola(id) ON DELETE CASCADE,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) UNIQUE,
    metodo_autenticacao VARCHAR(50) CHECK (metodo_autenticacao IN ('SENHA', 'MFA')) DEFAULT 'SENHA',
    senha VARCHAR(100),
    cpf VARCHAR(11) UNIQUE NOT NULL,
    perfil VARCHAR(50) CHECK (perfil IN ('MASTER', 'ADMIN', 'RESPONSAVEL', 'ALUNO', 'FUNCIONARIO', 'PDV', 'RESPONSAVEL_CONTRATUAL', 'PROFESSOR')) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO',
    primeiro_acesso BOOLEAN DEFAULT TRUE NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW()

    CONSTRAINT chk_usuario_perfil_escola CHECK (
        (perfil = 'MASTER' AND escola_id IS NULL) OR (perfil <> 'MASTER' AND escola_id IS NOT NULL)
    )
);

CREATE UNIQUE INDEX idx_usuario_uuid ON usuario(uuid);
CREATE UNIQUE INDEX idx_usuario_email ON usuario(email);
CREATE UNIQUE INDEX idx_usuario_telefone ON usuario(telefone);
CREATE INDEX idx_usuario_cpf ON usuario(cpf);

-- ==========================
-- TABELA ALUNO
-- ==========================
CREATE TABLE aluno (
    id BIGINT PRIMARY KEY,
    matricula VARCHAR(255),
    foto VARCHAR(255),
    criado_em TIMESTAMP DEFAULT NOW(),
    atualizado_em TIMESTAMP DEFAULT NOW(),
    version INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_aluno_id
        FOREIGN KEY (id) REFERENCES usuario(id)
        ON DELETE NO ACTION
);

-- Se quiser indexar 'matricula'
CREATE INDEX idx_aluno_matricula ON aluno(matricula);

CREATE TABLE responsavel_aluno (
    id SERIAL PRIMARY KEY,
    responsavel_id BIGINT NOT NULL,
    aluno_id BIGINT NOT NULL,
    grau_parentesco VARCHAR(30) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_responsavel_usuario FOREIGN KEY (responsavel_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_aluno_usuario FOREIGN KEY (aluno_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT uq_responsavel_aluno UNIQUE (responsavel_id, aluno_id)
);

-- ==========================
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
-- TRIGGER PRODUTO
-- ==========================
CREATE OR REPLACE FUNCTION trg_item_pedido_quantidade_vendida()
RETURNS TRIGGER
LANGUAGE plpgsql
AS $$
DECLARE
v_diff INTEGER;
BEGIN

    IF TG_OP = 'INSERT' THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida + NEW.quantidade
WHERE id = NEW.produto_id;

RETURN NEW;

ELSIF TG_OP = 'UPDATE' THEN
        IF OLD.produto_id <> NEW.produto_id THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida - OLD.quantidade
WHERE id = OLD.produto_id;


UPDATE produto
SET quantidade_vendida = quantidade_vendida + NEW.quantidade
WHERE id = NEW.produto_id;

ELSE

            v_diff := COALESCE(NEW.quantidade,0) - COALESCE(OLD.quantidade,0);

            IF v_diff <> 0 THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida + v_diff
WHERE id = NEW.produto_id;
END IF;
END IF;

RETURN NEW;

ELSIF TG_OP = 'DELETE' THEN
UPDATE produto
SET quantidade_vendida = quantidade_vendida - OLD.quantidade
WHERE id = OLD.produto_id;

RETURN OLD;
END IF;
END;
$$;

DROP TRIGGER IF EXISTS trg_item_pedido_quantidade_vendida
    ON item_pedido;

CREATE TRIGGER trg_item_pedido_quantidade_vendida
    AFTER INSERT OR UPDATE OR DELETE        -- um único trigger cobre tudo
                    ON item_pedido
                        FOR EACH ROW
                        EXECUTE FUNCTION trg_item_pedido_quantidade_vendida();

-- ==========================
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

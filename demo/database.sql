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
    criado_em TIMESTAMP DEFAULT NOW()
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
    perfil VARCHAR(50) CHECK (perfil IN ('MASTER', 'ADMIN', 'RESPONSAVEL', 'ALUNO', 'FUNCIONARIO', 'PDV')) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO',
    primeiro_acesso BOOLEAN DEFAULT TRUE NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP DEFAULT NOW(),

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
-- TABELA CARTÃO_ALUNO
-- ==========================
CREATE TABLE cartao_aluno (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    aluno_id BIGINT NOT NULL,
    numero VARCHAR(255) NOT NULL,
    senha VARCHAR(255) NOT NULL, 
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO' NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Chave estrangeira para a tabela aluno
    CONSTRAINT fk_cartao_aluno 
        FOREIGN KEY (aluno_id) REFERENCES aluno(id) ON DELETE CASCADE,
    
    -- Constraint de unicidade para o campo uuid
    CONSTRAINT uq_cartao_aluno_uuid UNIQUE (uuid)
);

-- Índices (além dos UNIQUE, que geram índices implícitos, 
-- podemos criar índices adicionais para buscas específicas)
CREATE INDEX idx_cartao_aluno_id     ON cartao_aluno(id);
CREATE INDEX idx_cartao_aluno_uuid   ON cartao_aluno(uuid);
CREATE INDEX idx_cartao_aluno_aluno  ON cartao_aluno(aluno_id);
CREATE INDEX idx_cartao_aluno_numero ON cartao_aluno (numero);

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
    quantidade_vendida BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_produto_escola FOREIGN KEY (escola_id) REFERENCES escola(id) ON DELETE CASCADE,
    CONSTRAINT uq_produto_uuid UNIQUE (uuid)
);

CREATE INDEX idx_produto_id ON produto(id);
CREATE INDEX idx_produto_uuid ON produto(uuid);
CREATE INDEX idx_produto_escola ON produto(escola_id);
CREATE INDEX idx_produto_departamento ON produto(departamento);
CREATE INDEX idx_produto_escola_departamento ON produto(escola_id, departamento);

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
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP NULL,
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
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NULL, -- Produto opcional
    descricao_produto VARCHAR(255) NOT NULL, -- Descrição independente do produto
    quantidade INT NOT NULL,
    valor_unitario NUMERIC(15,2) NOT NULL,
    valor_total NUMERIC(15,2) NOT NULL,
    version INT NOT NULL DEFAULT 0,
    criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Chave estrangeira com o pedido
    CONSTRAINT fk_item_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id) ON DELETE CASCADE,

    -- Chave estrangeira opcional com o produto
    CONSTRAINT fk_item_pedido_produto FOREIGN KEY (produto_id) REFERENCES produto(id) ON DELETE SET NULL
);

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
    version   INT          NOT NULL DEFAULT 0
);



-- ==========================
-- ÍNDICES INDIVIDUAIS
-- ==========================

-- Índice para busca rápida pelo pedido
CREATE INDEX idx_item_pedido_pedido_id ON item_pedido(pedido_id);

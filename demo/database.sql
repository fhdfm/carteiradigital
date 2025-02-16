CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE escolas (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
    nome VARCHAR(255) NOT NULL,
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO',
    version INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_escolas_uuid ON escolas(uuid);


CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE DEFAULT uuid_generate_v4(),
    escola_id INT REFERENCES escolas(id) ON DELETE CASCADE, -- permite NULL por padrão
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    telefone VARCHAR(20) UNIQUE,
    metodo_autenticacao VARCHAR(50) CHECK (metodo_autenticacao IN ('SENHA', 'MFA')) DEFAULT 'SENHA',
    senha VARCHAR(100) NOT NULL,
    cpf VARCHAR(11) UNIQUE,
    perfil VARCHAR(50) CHECK (perfil IN ('MASTER', 'ADMIN', 'RESPONSAVEL', 'ALUNO', 'FUNCIONARIO', 'PDV')) NOT NULL,
    status VARCHAR(20) CHECK (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO',
    version INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP DEFAULT NOW(),
    
    -- Nova constraint: se perfil for MASTER, escola_id deve ser NULL; caso contrário, deve ser preenchido.
    CONSTRAINT chk_usuarios_perfil_escola CHECK (
        (perfil = 'MASTER' AND escola_id IS NULL) OR (perfil <> 'MASTER' AND escola_id IS NOT NULL)
    )
);


CREATE UNIQUE INDEX idx_usuarios_uuid ON usuarios(uuid);
CREATE UNIQUE INDEX idx_usuarios_email ON usuarios(email);
CREATE UNIQUE INDEX idx_usuarios_telefone ON usuarios(telefone);
CREATE INDEX idx_usuarios_cpf ON usuarios (cpf);

CREATE TABLE alunos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    responsavel_id BIGINT NOT NULL,
    senha VARCHAR(255) NOT NULL,
    matricula VARCHAR(255),
    foto VARCHAR(255),
    version INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_alunos_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE NO ACTION,   -- Altere conforme desejado (CASCADE, SET NULL, etc.)

    CONSTRAINT fk_alunos_responsavel
        FOREIGN KEY (responsavel_id)
        REFERENCES usuarios(id)
        ON DELETE NO ACTION    -- Altere conforme desejado (CASCADE, SET NULL, etc.)
);

-- Cria índices
CREATE INDEX idx_alunos_usuario ON alunos(usuario_id);
CREATE INDEX idx_alunos_responsavel ON alunos(responsavel_id);
CREATE INDEX idx_alunos_matricula ON alunos(matricula);


CREATE TABLE cartoes_alunos (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    aluno_id BIGINT NOT NULL,
    numero VARCHAR(255) NOT NULL,
    status VARCHAR(20) (status IN ('ATIVO', 'INATIVO')) DEFAULT 'ATIVO' NOT NULL,
    version INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),
    
    -- Chave estrangeira para 'alunos'
    CONSTRAINT fk_cartao_aluno
        FOREIGN KEY (aluno_id)
        REFERENCES alunos(id)
        ON DELETE CASCADE,
    
    -- 'uuid' único
    CONSTRAINT uq_cartao_uuid UNIQUE (uuid),
    
    -- Restringe 'status' a ATIVO ou INATIVO
    CONSTRAINT chk_cartao_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

-- Índice em 'id' (PK já gera índice internamente, mas criamos explicitamente nomeado)
CREATE INDEX idx_cartoes_alunos_id
  ON cartoes_alunos(id);

-- Índice em 'uuid' 
-- (Observação: a UNIQUE constraint já gera um índice único automaticamente,
-- mas, se desejar nomear explicitamente, pode usar um UNIQUE INDEX ou esse índice simples)
CREATE INDEX idx_cartoes_alunos_uuid
  ON cartoes_alunos(uuid);

-- Índice em 'numero'
CREATE INDEX idx_cartoes_alunos_numero
  ON cartoes_alunos(numero);

-- Índice em 'aluno_id'
CREATE INDEX idx_cartoes_alunos_aluno
  ON cartoes_alunos(aluno_id);


-- Cria a tabela 'produtos'
CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL,
    escola_id INT NOT NULL,
    nome VARCHAR(255) NOT NULL,
    foto VARCHAR(255),
    preco DECIMAL(10,2) NOT NULL,
    departamento VARCHAR(50) NOT NULL,
    quantidade_vendidas BIGINT NOT NULL,
    version INT NOT NULL DEFAULT 1,
    criado_em TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_produtos_escola
        FOREIGN KEY (escola_id)
        REFERENCES escolas(id)
        ON DELETE CASCADE
);

-- Torna 'uuid' único
ALTER TABLE produtos
  ADD CONSTRAINT uq_produtos_uuid UNIQUE (uuid);

-- Índice em 'id' (lembrando que a PK já gera um índice interno, 
-- mas criado aqui para cumprir a requisição de ter um índice específico)
CREATE INDEX idx_produtos_id 
  ON produtos(id);

-- Índice em 'uuid' (também já existe indiretamente pelo UNIQUE, 
-- mas aqui para dar nome ao índice explicitamente)
CREATE INDEX idx_produtos_uuid
  ON produtos(uuid);

-- Índice em 'escola_id'
CREATE INDEX idx_produtos_escola
  ON produtos(escola_id);

-- Índice em 'departamento'
CREATE INDEX idx_produtos_departamento
  ON produtos(departamento);

-- Índice composto em (escola_id, departamento)
CREATE INDEX idx_produtos_escola_departamento
  ON produtos(escola_id, departamento);
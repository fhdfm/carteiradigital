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

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

-- Criação da tabela serie
CREATE TABLE serie (
                       id BIGSERIAL PRIMARY KEY,
                       uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
                       nome VARCHAR(255) NOT NULL,
                       turno VARCHAR(20) CHECK (turno IN ('MANHA', 'TARDE', 'NOITE')) NOT NULL,
                       criado_em TIMESTAMP DEFAULT NOW(),
                       atualizado_em TIMESTAMP DEFAULT NOW(),
                       version INT NOT NULL DEFAULT 0
);

-- Índice útil para consultas por nome
CREATE INDEX idx_serie_nome ON serie(nome);


-- Criação da tabela grade_horario
CREATE TABLE grade_horario (
                               id BIGSERIAL PRIMARY KEY,
                               uuid UUID DEFAULT uuid_generate_v4() UNIQUE NOT NULL,
                               dia VARCHAR(20) CHECK (dia IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA')) NOT NULL,
                               inicio TIME NOT NULL,
                               fim TIME NOT NULL,
                               serie_id BIGINT NOT NULL,
                               professor_id BIGINT NOT NULL,
                               disciplina_id BIGINT NOT NULL,
                               criado_em TIMESTAMP DEFAULT NOW(),
                               atualizado_em TIMESTAMP DEFAULT NOW(),
                               version INT NOT NULL DEFAULT 0,

                               CONSTRAINT fk_grade_horario_serie
                                   FOREIGN KEY (serie_id) REFERENCES serie(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_grade_horario_professor
                                   FOREIGN KEY (professor_id) REFERENCES professor(id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_grade_horario_disciplina
                                   FOREIGN KEY (disciplina_id) REFERENCES disciplina(id)
                                       ON DELETE CASCADE
);

-- Índices úteis
CREATE INDEX idx_grade_horario_serie ON grade_horario(serie_id);
CREATE INDEX idx_grade_horario_professor ON grade_horario(professor_id);
CREATE INDEX idx_grade_horario_disciplina ON grade_horario(disciplina_id);
CREATE INDEX idx_grade_horario_dia ON grade_horario(dia);

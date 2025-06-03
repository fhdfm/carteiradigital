-- Criação da tabela professor
CREATE TABLE professor (
                           id BIGINT PRIMARY KEY,
                           foto VARCHAR(255),
                           criado_em TIMESTAMP DEFAULT NOW(),
                           atualizado_em TIMESTAMP DEFAULT NOW(),
                           version INT NOT NULL DEFAULT 0,
                           CONSTRAINT fk_professor_id
                               FOREIGN KEY (id) REFERENCES usuario(id)
                                   ON DELETE NO ACTION
);

-- Índice para relacionamento com usuario
CREATE INDEX idx_professor_id ON professor(id);


-- Criação da tabela de associação entre professor e disciplina
CREATE TABLE professor_disciplina (
                                      professor_id BIGINT NOT NULL,
                                      disciplina_id BIGINT NOT NULL,
                                      PRIMARY KEY (professor_id, disciplina_id),
                                      CONSTRAINT fk_professor_disciplina_professor
                                          FOREIGN KEY (professor_id) REFERENCES professor(id)
                                              ON DELETE CASCADE,
                                      CONSTRAINT fk_professor_disciplina_disciplina
                                          FOREIGN KEY (disciplina_id) REFERENCES disciplina(id)
                                              ON DELETE CASCADE
);

-- Índices para junções rápidas
CREATE INDEX idx_professor_disciplina_professor ON professor_disciplina(professor_id);
CREATE INDEX idx_professor_disciplina_disciplina ON professor_disciplina(disciplina_id);


-- Criação da tabela de horários disponíveis do professor
CREATE TABLE horario_disponivel (
                                    id BIGSERIAL PRIMARY KEY,
                                    professor_id BIGINT NOT NULL,
                                    dia_semana VARCHAR(20) CHECK (dia_semana IN ('SEGUNDA', 'TERCA', 'QUARTA', 'QUINTA', 'SEXTA')) NOT NULL,
                                    turno VARCHAR(20) CHECK (turno IN ('MANHA', 'TARDE', 'NOITE')) NOT NULL,
                                    horario_inicio TIME NOT NULL,
                                    horario_fim TIME NOT NULL,
                                    criado_em TIMESTAMP DEFAULT NOW(),
                                    atualizado_em TIMESTAMP DEFAULT NOW(),
                                    version INT NOT NULL DEFAULT 0,
                                    CONSTRAINT fk_horario_disponivel_professor
                                        FOREIGN KEY (professor_id) REFERENCES professor(id)
                                            ON DELETE CASCADE
);

-- Índices úteis
CREATE INDEX idx_horario_disponivel_professor ON horario_disponivel(professor_id);
CREATE INDEX idx_horario_disponivel_dia_semana_turno ON horario_disponivel(dia_semana, turno);

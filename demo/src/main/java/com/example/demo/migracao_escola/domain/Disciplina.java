package com.example.demo.migracao_escola.domain;

import com.example.demo.domain.model.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "disciplina")
public class Disciplina extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @ManyToMany(mappedBy = "disciplinas")
    private List<Professor> professores = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<Professor> getProfessores() {
        return professores;
    }

    public void setProfessores(List<Professor> professores) {
        this.professores = professores;
    }

    @Override
    protected UUID getUuid() {
        return super.uuid;
    }

    @Override
    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    protected LocalDateTime getCriadoEm() {
        return super.criadoEm;
    }

    @Override
    protected void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    protected LocalDateTime getAtualizadoEm() {
        return super.atualizadoEm;
    }

    @Override
    protected void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}
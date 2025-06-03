package com.example.demo.migracao_escola.domain;

import com.example.demo.domain.model.BaseEntity;
import com.example.demo.migracao_escola.domain.enums.Turno;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "serie")
public class Serie extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Enumerated(EnumType.STRING)
    private Turno turno;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<GradeHorario> gradeHorarios = new ArrayList<>();

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

    public Turno getTurno() {
        return turno;
    }

    public void setTurno(Turno turno) {
        this.turno = turno;
    }

    public List<GradeHorario> getGradeHorarios() {
        return gradeHorarios;
    }

    public void setGradeHorarios(List<GradeHorario> gradeHorarios) {
        this.gradeHorarios = gradeHorarios;
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
package com.example.demo.migracao_escola.domain;

import com.example.demo.domain.model.BaseEntity;
import com.example.demo.migracao_escola.domain.enums.DiaSemana;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "grade_horario")
public class GradeHorario extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DiaSemana dia;

    private LocalTime inicio;
    private LocalTime fim;

    @ManyToOne
    @JoinColumn(name = "serie_id")
    private Serie serie;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToOne
    @JoinColumn(name = "disciplina_id")
    private Disciplina disciplina;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiaSemana getDia() {
        return dia;
    }

    public void setDia(DiaSemana dia) {
        this.dia = dia;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public LocalTime getFim() {
        return fim;
    }

    public void setFim(LocalTime fim) {
        this.fim = fim;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie(Serie serie) {
        this.serie = serie;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
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
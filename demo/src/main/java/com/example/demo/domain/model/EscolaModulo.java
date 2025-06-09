package com.example.demo.domain.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class EscolaModulo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Escola escola;

    @ManyToOne
    private Modulo modulo;

    private boolean ativo;

    private LocalDate dataAtivacao;

    private LocalDate dataExpiracao;

    public boolean isExpirado() {
        return dataExpiracao != null && dataExpiracao.isBefore(LocalDate.now());
    }

    public boolean isValido() {
        return ativo && !isExpirado();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public LocalDate getDataAtivacao() {
        return dataAtivacao;
    }

    public void setDataAtivacao(LocalDate dataAtivacao) {
        this.dataAtivacao = dataAtivacao;
    }

    public LocalDate getDataExpiracao() {
        return dataExpiracao;
    }

    public void setDataExpiracao(LocalDate dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
}

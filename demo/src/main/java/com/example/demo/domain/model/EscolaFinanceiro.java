package com.example.demo.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "escola_financeiro")
public class EscolaFinanceiro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "escola_id", nullable = false, unique = true)
    private Escola escola;

    @Column(name = "dia_pagamento", nullable = false)
    private Integer diaPagamento;

    @Column(name = "dia_recebimento", nullable = false)
    private Integer diaRecebimento;

    @Version
    private int version = 0;

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public Escola getEscola() {
        return escola;
    }

    public void setEscola(Escola escola) {
        this.escola = escola;
    }

    public Integer getDiaPagamento() {
        return diaPagamento;
    }

    public void setDiaPagamento(Integer diaPagamento) {
        this.diaPagamento = diaPagamento;
    }

    public Integer getDiaRecebimento() {
        return diaRecebimento;
    }

    public void setDiaRecebimento(Integer diaRecebimento) {
        this.diaRecebimento = diaRecebimento;
    }
}

package com.example.demo.domain.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "aluno")
@PrimaryKeyJoinColumn(name = "id")
public class Aluno extends Usuario {

    @Column(name = "matricula")
    private String matricula;

    @Column(name = "foto")
    private String foto;

    @OneToMany(mappedBy = "aluno", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Cartao> cartoes = new ArrayList<>();

    @OneToMany(mappedBy = "aluno", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponsavelAluno> responsaveis = new ArrayList<>();

    public Aluno() {
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setCartoes(List<Cartao> cartoes) {
        this.cartoes = cartoes;
    }

    public List<Cartao> getCartoes() {
        return cartoes;
    }

    public List<ResponsavelAluno> getResponsaveis() {
        return responsaveis;
    }
    
    public void setResponsaveis(List<ResponsavelAluno> responsaveis) {
        this.responsaveis = responsaveis;
    }
}

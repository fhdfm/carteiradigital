package com.example.demo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.demo.domain.enums.Status;

import com.example.demo.domain.model.carteira.Carteira;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "cartao_carteira")
public class Cartao extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carteira_id", nullable = false)
    private Carteira carteira;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "senha", nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Version
    private int version;

    public Cartao() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Carteira getCarteira() {
        return carteira;
    }

    public void setCarteira(Carteira carteira) {
        this.carteira = carteira;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getSenha() {
        return senha;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    protected UUID getUuid() {
        return super.uuid;
    }

    @Override
    protected void setUuid(UUID uuid) {
        super.uuid = uuid;
    }

    @Override
    protected LocalDateTime getCriadoEm() {
        return super.criadoEm;
    }

    @Override
    protected void setCriadoEm(LocalDateTime criadoEm) {
        super.criadoEm = criadoEm;
    }

    @Override
    protected LocalDateTime getAtualizadoEm() {
        return super.atualizadoEm;
    }

    @Override
    protected void setAtualizadoEm(LocalDateTime atualizadoEm) {
        super.atualizadoEm = atualizadoEm;
    }
}

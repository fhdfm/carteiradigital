package com.example.demo.domain.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pagamento")
public class Pagamento extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuarioPagante;

    @Column(name = "mp_id", nullable = false)
    private String mpId;
    @Column(name = "status")
    private String status;

    @Column(name = "data")
    private LocalDateTime data;
    @Transient
    private BigDecimal valorTotal;

    @OneToMany(mappedBy = "pagamento", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<PagamentoItem> itens;

    public Pagamento() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    public Usuario getUsuarioPagante() {
        return usuarioPagante;
    }

    public void setUsuarioPagante(Usuario usuarioPagante) {
        this.usuarioPagante = usuarioPagante;
    }

    public String getMpId() {
        return mpId;
    }

    public void setMpId(String mpId) {
        this.mpId = mpId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public List<PagamentoItem> getItens() {
        return itens;
    }

    public void setItens(List<PagamentoItem> itens) {
        this.itens = itens;
    }

    public boolean isAprovado() {
        return "approved".equals(status);
    }
}

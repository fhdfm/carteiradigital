package com.example.demo.domain.enums;

public enum TipoPagamento {
    RECARGA_CARTAO("Recarga do Cartão"),
    MENSALIDADE("Mensalidade");

    private final String descricao;

    TipoPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

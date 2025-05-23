package com.example.demo.dto.projection;

import com.example.demo.dto.projection.produto.CategoriaSummary;

import java.util.UUID;

public interface ProdutoView {

    UUID getUuid();

    String getNome();

    CategoriaSummary getCategoria();
//
//    String getFoto();
//
//    BigDecimal getPreco();
//
//    Departamento getDepartamento();
//
//    Long getQuantidadeVendida();
//
//
//    String getEscolaNome();
}

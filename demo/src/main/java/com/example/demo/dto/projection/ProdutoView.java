package com.example.demo.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

import com.example.demo.domain.enums.Departamento;
import com.example.demo.dto.projection.produto.CategoriaSummary;

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

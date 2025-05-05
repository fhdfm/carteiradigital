package com.example.demo.dto.projection.produto;

import com.example.demo.dto.projection.escola.EscolaIdAndName;

import java.util.UUID;

public interface CategoriaSummary {
    EscolaIdAndName getEscola();

    UUID getUuid();

    String getNome();
}

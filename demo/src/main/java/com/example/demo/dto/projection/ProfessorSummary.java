package com.example.demo.dto.projection;

import java.util.UUID;

import com.example.demo.dto.projection.escola.EscolaIdAndName;

public interface ProfessorSummary {
    EscolaIdAndName getEscola();
    UUID getUuid();
    String getNome();
    String getEmail();
    String getCpf();
    String getRegistroFuncional();
}

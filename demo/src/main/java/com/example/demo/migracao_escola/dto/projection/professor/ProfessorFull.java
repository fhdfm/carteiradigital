package com.example.demo.migracao_escola.dto.projection.professor;

import java.util.UUID;

import com.example.demo.dto.projection.escola.EscolaIdAndName;

public interface ProfessorFull {
    EscolaIdAndName getEscola();
    UUID getUuid();
    String getNome();
    String getEmail();
    String getCpf();
    String getRegistroFuncional();
    String getFoto();
}

package com.example.demo.dto.projection.aluno;

import java.util.UUID;

import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.usuario.UsuarioIdAndName;

public interface AlunoSummary {
    EscolaIdAndName getEscola();
    UsuarioIdAndName getResponsavel();
    UUID getUuid();
    String getNome();
    String getEmail();
    String getCpf();
    String getMatricula();
}

package com.example.demo.dto;

import com.example.demo.domain.enums.GrauParentesco;

public record ResponsavelAlunoRequest(GrauParentesco grauParentesco, UsuarioRequest responsavel) {
    
}

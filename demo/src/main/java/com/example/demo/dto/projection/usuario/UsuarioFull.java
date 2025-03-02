package com.example.demo.dto.projection.usuario;

import java.util.UUID;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.dto.projection.escola.EscolaCombo;

public interface UsuarioFull {
    EscolaCombo getEscola();
    UUID getUuid();
    String getNome();
    String getEmail();
    String getTelefone();
    String getCpf();
    Perfil getPerfil();
    Status getStatus();
}

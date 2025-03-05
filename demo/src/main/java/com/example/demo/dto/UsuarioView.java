package com.example.demo.dto;

import com.example.demo.domain.enums.Perfil;

public record UsuarioView(
        String nome,

        String email,

        Perfil perfil

) {
}

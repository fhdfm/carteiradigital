package com.example.demo.dto;

import com.example.demo.domain.enums.Perfil;

public record CurrentUserView(
        String nome,

        String email,

        Perfil perfil
) {
}

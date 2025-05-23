package com.example.demo.dto;

import java.util.UUID;

public record AlunoUsuarioResponse(
        UUID uuid,
        String nome,
        String matricula,
        String email,
        String foto
) {
}

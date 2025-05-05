package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(
        @Schema(description = "Nome da categoria", example = "Snacks")
        @NotBlank(message = "Nome deve ser preenchido")
        @Size(min = 3, message = "Nome deve ter pelo menos 3 caracteres")
        String nome
) {
}

package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CartaoCadastroRequest(
        @Schema(description = "UUID do aluno", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "UUID do aluno deve ser preenchido")
        UUID uuid,
        @Schema(description = "Número do cartão", example = "00000000000")
        @NotBlank(message = "Número do cartão deve ser preenchido")
        @Size(min = 1, max = 30, message = "Número deve ter entre 1 e 30 caracteres")
        String numero
) {
}

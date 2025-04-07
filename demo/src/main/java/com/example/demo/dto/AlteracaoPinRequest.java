package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record AlteracaoPinRequest(
        @Schema(description = "UUID do aluno", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "UUID do aluno deve ser preenchido")
        UUID uuid,
        @Schema(description = "Nova senha do cartão", example = "0000")
        @NotBlank(message = "Nova senha do cartão deve ser preenchida")
        @Size(min = 4, max = 4, message = "Senha deve ter 4 digitos")
        String senha
) {
}

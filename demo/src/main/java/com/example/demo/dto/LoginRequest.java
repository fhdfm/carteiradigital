package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Schema(description = "Login", example = "user")
        @NotBlank(message = "Login deve ser preenchido")
        String login,
        @Schema(description = "Senha", example = "root")
        @NotBlank(message = "Senha deve ser preenchida")
        String password
) {

}

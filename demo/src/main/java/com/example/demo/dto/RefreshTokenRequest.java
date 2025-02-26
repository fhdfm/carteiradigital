package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequest(
        @Schema(description = "Token", example = "token")
        @NotBlank(message = "Token deve ser enviado")
        String token
) {
    
}

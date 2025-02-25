package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record EscolaParametrosRequest(
        @Schema(description = "Chave de pagamento", example = "Chave da Escola")
        @NotBlank(message = "Chave de pagamento deve ser preenchida")
        String paymentSecret
) {

}

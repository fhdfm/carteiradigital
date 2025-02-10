package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record EscolaRequest(
        @Schema(description = "Nome da escola", example = "Nome da Escola")
        String nome,
        @Schema(description = "CNPJ da escola", example = "00.000.000/0001-00")
        String cnpj
) {
    
}

package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record EscolaRequest(
        @Schema(description = "Nome da escola", example = "Nome da Escola")
        @NotBlank(message = "Nome deve ser preenchido")
        @Size(min = 5, message = "Nome deve ter pelo menos 5 caracteres")
        String nome,
        @Schema(description = "CNPJ da escola", example = "00.000.000/0001-00")
        @NotBlank(message = "CNPJ deve ser preenchido")
        @Pattern(
                regexp = "\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
                message = "CNPJ deve seguir o padrão 00.000.000/0001-00"
        )
        String cnpj
) {
    
}

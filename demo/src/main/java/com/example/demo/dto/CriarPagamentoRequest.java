package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.enums.TipoPagamento;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record CriarPagamentoRequest(

        @Schema(description = "UUID do Aluno", example = "00000000-0000-0000-0000-000000000000")
        @NotBlank(message = "UUID do aluno deve ser preenchido")
        UUID alunoUUID,
        @Schema(description = "Valor do pagamento", example = "50.00")
        @NotBlank(message = "Valor do pagamento deve ser preenchido")
        Double valor,
        @Schema(description = "Tipo de pagamento", example = "RECARGA_CARTAO")
        @NotBlank(message = "Tipo de pagamento deve ser preenchido")
        TipoPagamento tipo
) {
    
}

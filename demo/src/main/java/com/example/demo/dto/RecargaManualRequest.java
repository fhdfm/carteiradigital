package com.example.demo.dto;

import com.example.demo.domain.enums.TipoPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

public record RecargaManualRequest(

        @Schema(description = "Valor da recarga", example = "50.00")
        @NotBlank(message = "Valor da recarga deve ser preenchido")
        BigDecimal valor
) {
    
}

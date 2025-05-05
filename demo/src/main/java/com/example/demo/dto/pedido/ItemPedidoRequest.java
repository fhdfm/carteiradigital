package com.example.demo.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemPedidoRequest(

        @Schema(description = "UUID do produto", example = "550e8400‑e29b‑41d4‑a716‑446655440000")
        @NotNull(message = "Produto deve ser informado")
        UUID produtoId,

        @Schema(description = "Quantidade do produto", example = "2")
        @NotNull(message = "Quantidade deve ser informada")
        @Positive(message = "Quantidade deve ser positiva")
        Integer quantidade,

        @Schema(description = "Valor unitário do produto no momento da venda", example = "1999.90")
        @NotNull(message = "Valor unitário deve ser informado")
        @Positive(message = "Valor unitário deve ser positivo")
        BigDecimal valorUnitario
) {
}

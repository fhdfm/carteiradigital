package com.example.demo.dto.pedido;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record PedidoRequest(

        @Schema(description = "UUID do aluno comprador", example = "92b0f4ba‑42d5‑4bb3‑8534‑e0fb84efe7d7")
        @NotNull(message = "Comprador deve ser informado")
        UUID compradorId,

        @Schema(description = "Itens que compõem o pedido")
        @NotEmpty(message = "Pedido deve conter ao menos um item")
        List<@Valid ItemPedidoRequest> itens
) {
}

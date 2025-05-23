package com.example.demo.dto.projection.pedido;

import java.math.BigDecimal;
import java.util.UUID;

interface ItemPedidoView {
    String getDescricao();

    Integer getQuantidade();

    BigDecimal getValorUnitario();

    BigDecimal getValorTotal();
}

package com.example.demo.dto.projection.pedido;

import java.math.BigDecimal;
import java.util.UUID;

interface ItemPedidoView {
    UUID getProdutoUuid();      // produto.uuid

    String getDescricao();

    Integer getQuantidade();

    BigDecimal getValorUnitario();

    BigDecimal getValorTotal();
}

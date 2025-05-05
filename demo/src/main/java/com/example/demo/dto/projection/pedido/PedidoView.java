package com.example.demo.dto.projection.pedido;

import com.example.demo.domain.enums.StatusPedido;
import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.usuario.UsuarioIdAndName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PedidoView {

    UUID getUuid();

    EscolaIdAndName getEscola();

    UsuarioIdAndName getComprador();

    UsuarioIdAndName getVendedor();

    BigDecimal getTotal();

    StatusPedido getStatus();

    LocalDateTime getCriadoEm();

    LocalDateTime getAtualizadoEm();

    /**
     * Itens que compõem o pedido (opcional; remova se não precisar).
     */
    List<ItemPedidoView> getItens();
}

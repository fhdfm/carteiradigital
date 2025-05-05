package com.example.demo.repository.specification;

import com.example.demo.domain.enums.StatusPedido;
import com.example.demo.domain.model.Pedido;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Filtros dinâmicos para buscas de Pedido.
 */
@ParameterObject
public class PedidoSpecification implements Specification<Pedido> {

    @Schema(description = "UUID da escola", example = "e0fb84ef-e7d7-4bb3-8534-92b0f4ba42d5")
    private UUID escolaId;

    @Schema(description = "UUID do aluno comprador", example = "b3b0d2e4-75a6-4bf5-9f67-d1efc1e6371c")
    private UUID compradorId;

    @Schema(description = "UUID do vendedor", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID vendedorId;

    @Schema(description = "Status do pedido", example = "CONFIRMADO")
    private StatusPedido status;

    public PedidoSpecification(UUID escolaId,
                               UUID compradorId,
                               UUID vendedorId,
                               StatusPedido status) {
        this.escolaId = escolaId;
        this.compradorId = compradorId;
        this.vendedorId = vendedorId;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<Pedido> root,
                                 CriteriaQuery<?> query,
                                 CriteriaBuilder cb) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(escolaId)) {
            predicates.add(cb.equal(root.get("escola").get("uuid"), escolaId));
        }

        if (Objects.nonNull(compradorId)) {
            predicates.add(cb.equal(root.get("comprador").get("uuid"), compradorId));
        }

        if (Objects.nonNull(vendedorId)) {
            predicates.add(cb.equal(root.get("vendedor").get("uuid"), vendedorId));
        }

        if (Objects.nonNull(status)) {
            predicates.add(cb.equal(root.get("status"), status));
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    /* getters (úteis para Swagger / SpringDoc) */

    public UUID getEscolaId() {
        return escolaId;
    }

    public UUID getCompradorId() {
        return compradorId;
    }

    public UUID getVendedorId() {
        return vendedorId;
    }

    public StatusPedido getStatus() {
        return status;
    }
}

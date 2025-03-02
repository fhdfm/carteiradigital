package com.example.demo.repository.specification;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Produto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@ParameterObject
public class ProdutoSpecification implements Specification<Produto> {

    @Schema(description = "Nome do produto", example = "Notebook Gamer")
    private String nome;

    @Schema(description = "UUID da categoria", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID categoriaId;

    @Schema(description = "Preço mínimo", example = "1000.00")
    private BigDecimal precoMinimo;

    @Schema(description = "Preço máximo", example = "5000.00")
    private BigDecimal precoMaximo;

    @Schema(description = "Status do produto", example = "ATIVO")
    private Status status;

    public ProdutoSpecification(String nome, UUID categoriaId, BigDecimal precoMinimo, BigDecimal precoMaximo, Status status) {
        this.nome = nome;
        this.categoriaId = categoriaId;
        this.precoMinimo = precoMinimo;
        this.precoMaximo = precoMaximo;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<Produto> root, CriteriaQuery<?> query, CriteriaBuilder criteria) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(nome) && !nome.isEmpty()) {
            predicates.add(criteria.like(root.get("nome"), "%" + nome + "%"));
        }

        if (Objects.nonNull(categoriaId)) {
            predicates.add(criteria.equal(root.get("categoria").get("uuid"), categoriaId));
        }

        if (Objects.nonNull(precoMinimo)) {
            predicates.add(criteria.greaterThanOrEqualTo(root.get("preco"), precoMinimo));
        }

        if (Objects.nonNull(precoMaximo)) {
            predicates.add(criteria.lessThanOrEqualTo(root.get("preco"), precoMaximo));
        }

        if (Objects.nonNull(status)) {
            predicates.add(criteria.equal(root.get("status"), status));
        }

        return criteria.and(predicates.toArray(new Predicate[0]));
    }

    public String getNome() {
        return nome;
    }

    public UUID getCategoriaId() {
        return categoriaId;
    }

    public BigDecimal getPrecoMinimo() {
        return precoMinimo;
    }

    public BigDecimal getPrecoMaximo() {
        return precoMaximo;
    }

    public Status getStatus() {
        return status;
    }
}

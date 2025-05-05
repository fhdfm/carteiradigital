package com.example.demo.repository.specification;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.CategoriaProduto;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
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

@ParameterObject
public class CategoriaProdutoSpecification implements Specification<CategoriaProduto> {

    @Schema(description = "UUID da Escola", example = "00000000-0000-0000-0000-000000000000")
    private UUID escolaId;

    @Schema(description = "Nome da categoria", example = "Snacks")
    private String nome;

    @Schema(description = "Status da categoria", example = "ATIVO")
    private Status status;

    public CategoriaProdutoSpecification(UUID escolaId, String nome, Status status) {
        this.escolaId = escolaId;
        this.nome = nome;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<CategoriaProduto> root, CriteriaQuery<?> query, CriteriaBuilder criteria) {

        List<Predicate> predicates = new ArrayList<>();

        // Se for ADMIN ou FUNCIONARIO, força o filtro para a escola do usuário logado
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        if (currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) {
            this.escolaId = currentUser.getEscola().getUuid();
        }

        // Se houver valor para escolaId (seja vindo do parâmetro ou do usuário logado) adiciona o predicate de filtro
        if (Objects.nonNull(escolaId)) {
            predicates.add(criteria.equal(root.get("escola").get("uuid"), this.escolaId));
        }

        if (Objects.nonNull(nome) && !nome.isEmpty()) {
            predicates.add(criteria.like(root.get("nome"), "%" + this.nome + "%"));
        }

        if (Objects.nonNull(status)) {
            predicates.add(criteria.equal(root.get("status"), this.status));
        } else {
            predicates.add(criteria.notEqual(root.get("status"), Status.INATIVO));
        }

        return criteria.and(predicates.stream().toArray(Predicate[]::new));
    }

    public UUID getEscolaId() {
        return escolaId;
    }

    public String getNome() {
        return nome;
    }

    public Status getStatus() {
        return status;
    }

}

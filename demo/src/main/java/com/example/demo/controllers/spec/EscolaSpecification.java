package com.example.demo.controllers.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.jpa.domain.Specification;

import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Status;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class EscolaSpecification implements Specification<Escola> {

    private String nome;
    private String cnpj;
    private Status status;

    public EscolaSpecification(String nome, String cnpj, Status status) {
        this.nome = nome;
        this.cnpj = cnpj;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<Escola> root, CriteriaQuery<?> query, CriteriaBuilder criteria) {

        List<Predicate> predicates = new ArrayList<>();

        if (Objects.nonNull(nome) && !nome.isEmpty()) {
            predicates.add(
                criteria.like(root.get("nome"), "%" +nome+ "%"));
        }

        if (Objects.nonNull(cnpj) && !cnpj.isEmpty()) {
            predicates.add(
                criteria.like(root.get("cnpj"), "%" +cnpj+ "%"));
        }

        if (Objects.nonNull(status)) {
            predicates.add(
                criteria.equal(root.get("status"), status));
        }

        return criteria.and(predicates.stream().toArray(Predicate[]::new));
    }
    
}

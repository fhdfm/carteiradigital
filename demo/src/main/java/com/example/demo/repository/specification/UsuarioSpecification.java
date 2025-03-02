package com.example.demo.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.jpa.domain.Specification;

import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Usuario;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@ParameterObject
public class UsuarioSpecification implements Specification<Usuario> {

    private UUID escolaId;
    private String nome;
    private String cpf;
    private String email;
    private Perfil perfil;
    private Status status;

    public UsuarioSpecification(String nome, String cpf, String email, Perfil perfil, Status status) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.perfil = perfil;
        this.status = status;
    }

    @Override
    public Predicate toPredicate(Root<Usuario> root, CriteriaQuery<?> query, CriteriaBuilder criteria) {
        
        List<Predicate> predicates = new ArrayList<>();

        // Se for ADMIN ou FUNCIONARIO, força o filtro para a escola do usuário logado
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        if (currentUser.possuiPerfil(Perfil.ADMIN) || currentUser.possuiPerfil(Perfil.FUNCIONARIO)) {
            this.escolaId = currentUser.getEscolaUuid();
        }

        // Se houver valor para escolaId (seja vindo do parâmetro ou do usuário logado) adiciona o predicate de filtro
        if (Objects.nonNull(escolaId)) {
            predicates.add(criteria.equal(root.get("escola").get("uuid"), this.escolaId));
        }

        if (Objects.nonNull(nome) && !nome.isEmpty()) {
            predicates.add(criteria.like(root.get("nome"), "%" + this.nome + "%"));
        }

        if (Objects.nonNull(cpf) && !cpf.isEmpty()) {
            predicates.add(criteria.like(root.get("cpf"), "%"
                + this.cpf.replaceAll("\\D", "") + "%"));
        }

        if (Objects.nonNull(email) && !email.isEmpty()) {
            predicates.add(criteria.like(root.get("email"), "%" + this.email + "%"));
        }

        // Garante que aluno não será exibido.
        predicates.add(criteria.notEqual(root.get("perfil"), Perfil.ALUNO));

        if (Objects.nonNull(perfil)) {
            if (perfil != Perfil.ALUNO) {
                predicates.add(criteria.equal(root.get("perfil"), this.perfil));
            }
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

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public Perfil getPerfil() {
        return perfil;
    }

    public Status getStatus() {
        return status;
    }
    
}

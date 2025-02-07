package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import com.example.demo.dto.EscolaView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.entity.Escola;

@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long>, JpaSpecificationExecutor<Escola> {

    Optional<Escola> findByUuid(UUID uuid);

    Optional<Escola> findByCnpj(String cnpj);

//    <T> Page<T> findAll(EscolaSpecification specification, Pageable pageable, Class<T> clazz);

    Page<Escola> findAll(Specification<Escola> specification, Pageable pageable);



}

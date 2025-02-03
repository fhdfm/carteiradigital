package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.entity.Escola;

@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long>, JpaSpecificationExecutor<Escola> {

    Optional<Escola> findByUuid(UUID uuid);

    Page<Escola> findAll(EscolaSpecification specification, Pageable pageable);
        
}

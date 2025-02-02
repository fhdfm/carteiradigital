package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Status;

@Repository
public interface EscolaRepository extends JpaRepository<Escola, Long> {

    Optional<Escola> findByUuid(UUID uuid);

    Page<Escola> findByStatus(Pageable pageable, Status status);
        
}

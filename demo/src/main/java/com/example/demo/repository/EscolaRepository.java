package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Escola;

@Repository
public interface EscolaRepository extends BaseRepository<Escola, Long> {

    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);

    Optional<Escola> findByUuid(UUID uuid);

    Optional<Escola> findByCnpj(String cnpj);
}

package com.example.demo.repositories;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Escola;

@Repository
public interface EscolaRepository extends BaseRepository<Escola, Long> {

    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);

    Optional<Escola> findByUuid(UUID uuid);

    Optional<Escola> findByCnpj(String cnpj);
}

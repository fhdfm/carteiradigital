package com.example.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Escola;
import com.example.demo.dto.projection.escola.EscolaIdAndName;

@Repository
public interface EscolaRepository extends BaseRepository<Escola, Long> {

    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);

    Optional<Escola> findByUuid(UUID uuid);

    Optional<Escola> findByCnpj(String cnpj);

    @Query("""
        SELECT e.uuid AS uuid, e.nome AS nome 
        FROM Escola e 
        WHERE e.status = 'ATIVO' 
        ORDER BY e.nome ASC
    """)
    List<EscolaIdAndName> findAllProjected();
}

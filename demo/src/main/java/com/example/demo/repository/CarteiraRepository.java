package com.example.demo.repository;

import com.example.demo.domain.model.carteira.Carteira;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarteiraRepository extends BaseRepository<Carteira, Long> {
    <T> Optional<T> findByAlunoUuid(UUID alunoUuid, Class<T> projectionClass);
    Optional<Carteira> findByAlunoUuid(UUID alunoUuid);
}

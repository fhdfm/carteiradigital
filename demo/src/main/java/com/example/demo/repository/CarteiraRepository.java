package com.example.demo.repository;

import com.example.demo.domain.model.carteira.Carteira;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarteiraRepository extends BaseRepository<Carteira, Long> {
    <T> Optional<T> findByAluno_Uuid(UUID alunoUuid, Class<T> projectionClass);

    Optional<Carteira> findByAluno_Uuid(UUID alunoUuid);
}

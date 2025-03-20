package com.example.demo.repository;

import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.domain.model.carteira.Transacao;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransacaoRepository extends BaseRepository<Transacao, Long> {
    <T> Optional<T> findByAlunoUuid(UUID alunoUuid, Class<T> projectionClass);
    Optional<Carteira> findByAlunoUuid(UUID alunoUuid);
}

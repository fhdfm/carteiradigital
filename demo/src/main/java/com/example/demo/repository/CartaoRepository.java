package com.example.demo.repository;

import com.example.demo.domain.enums.Status;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Cartao;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartaoRepository extends BaseRepository<Cartao, Long> {
    Optional<Cartao> findByStatusAndCarteira_Aluno_Uuid(Status status, UUID alunoUuid);

}

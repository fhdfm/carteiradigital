package com.example.demo.migracao_escola.repository;

import com.example.demo.migracao_escola.domain.Professor;
import com.example.demo.repository.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfessorRepository extends BaseRepository<Professor, Long> {

    boolean existsByEmail(String email);

    Optional<Professor> findByUuid(UUID uuid);

    boolean existsByCpf(String cpf);

    Optional<Professor> findByEmail(String email);

    boolean existsByCpfAndUuidNot(String cpf, UUID uuid);

    boolean existsByEmailAndUuidNot(String email, UUID uuid);

}

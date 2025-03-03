package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Aluno;

@Repository
public interface AlunoRepository extends BaseRepository<Aluno, Long> {

    @Query(value = """
    SELECT EXISTS(
        SELECT 1
        FROM aluno a
        WHERE a.responsavel_id = :responsavelId
    )
    """, nativeQuery = true)
    boolean existsByResponsavelId(@Param("responsavelId") UUID responsavelId);

    Optional<Aluno> findByUuid(UUID uuid);

    @Query("""
        SELECT a FROM Aluno a
        JOIN FETCH a.responsavel
        WHERE a.uuid = :uuid
    """)
    Optional<Aluno> findWithResponsavelByUuid(@Param("uuid") UUID uuid);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);

    Optional<Aluno> findByEmail(String email);

    boolean existsByCpfAndUuidNot(String cpf, UUID uuid);

    boolean existsByEmailAndUuidNot(String email, UUID uuid);

}

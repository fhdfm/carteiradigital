package com.example.demo.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Aluno;

@Repository
public interface AlunoRepository extends BaseRepository<Aluno, Long> {
 
    @Query(value = "SELECT EXISTS (" +
                   "SELECT 1 FROM aluno a " +
                   "INNER JOIN usuario u ON a.usuario_id = u.id " +
                   "INNER JOIN usuario r ON a.responsavel_id = r.id " +
                   "WHERE u.uuid = :alunoId AND r.uuid = :responsavelId" +
                   ")", nativeQuery = true)
    boolean isAlunoDoResponsavel(@Param("alunoId") UUID alunoId, 
                  @Param("responsavelId") UUID responsavelId);


}

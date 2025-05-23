package com.example.demo.repository;

import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.carteira.Carteira;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CarteiraRepository extends BaseRepository<Carteira, Long> {
    <T> Optional<T> findByAluno_Uuid(UUID alunoUuid, Class<T> projectionClass);

    Optional<Carteira> findByAluno_Uuid(UUID alunoUuid);

    @Query("""
       select a from Aluno a
       join Carteira c on a.id = c.aluno.id
       join Cartao cc on c.id = cc.carteira.id
       where cc.numero = :numero
    """)
    Aluno buscarAlunoPorNumeroCartao(@Param("numero") String numero);
}

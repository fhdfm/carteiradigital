package com.example.demo.repository;

import com.example.demo.domain.model.carteira.Carteira;
import com.example.demo.dto.AlunoUsuarioResponse;
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
         select new com.example.demo.dto.AlunoUsuarioResponse(
               a.uuid,
               u.nome,
               a.matricula,
               u.email,
               a.foto
           ) from Aluno a
       join Usuario u on a.id = u.id
       join Carteira c on a.id = c.aluno.id
       join Cartao cc on c.id = cc.carteira.id
       where cc.numero = :numero
    """)
    AlunoUsuarioResponse buscarAlunoPorNumeroCartao(@Param("numero") String numero);
}

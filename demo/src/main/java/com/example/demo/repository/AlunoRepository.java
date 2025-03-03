package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Usuario;

@Repository
public interface AlunoRepository extends BaseRepository<Aluno, Long> {

    boolean existsByResponsavel(Usuario responsavel);

}

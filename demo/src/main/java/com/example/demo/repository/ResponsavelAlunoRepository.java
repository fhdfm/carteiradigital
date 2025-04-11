package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.ResponsavelAluno;

@Repository
public interface ResponsavelAlunoRepository extends BaseRepository<ResponsavelAluno, Long> {

    boolean existsByResponsavelAndAluno(Long responsavel, Long alunoId);
    
}

package com.example.demo.repositories;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Aluno;

@Repository
public interface AlunoRepository extends BaseRepository<Aluno, Long> {
    
}

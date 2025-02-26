package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Aluno;

@Repository
public interface AlunoRepository extends BaseRepository<Aluno, Long> {

}

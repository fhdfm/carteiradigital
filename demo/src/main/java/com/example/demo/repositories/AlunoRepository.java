package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Aluno;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {
    
}

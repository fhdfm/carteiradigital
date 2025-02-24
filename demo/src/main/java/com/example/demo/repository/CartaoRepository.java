package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Cartao;

@Repository
public interface CartaoRepository extends BaseRepository<Cartao, Long> {
    
}

package com.example.demo.repositories;

import org.springframework.stereotype.Repository;

import com.example.demo.entity.Cartao;

@Repository
public interface CartaoRepository extends BaseRepository<Cartao, Long> {
    
}

package com.example.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    
}

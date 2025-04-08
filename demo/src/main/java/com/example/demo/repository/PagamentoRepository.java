package com.example.demo.repository;

import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.Pagamento;

@Repository
public interface PagamentoRepository extends BaseRepository<Pagamento, Long> {
    
}

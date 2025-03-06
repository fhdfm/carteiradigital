package com.example.demo.repository;

import com.example.demo.domain.model.Cartao;
import com.example.demo.domain.model.Pagamento;
import org.springframework.stereotype.Repository;

@Repository
public interface PagamentoRepository extends BaseRepository<Pagamento, Long> {
    
}

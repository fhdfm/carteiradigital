package com.example.demo.repository;

import com.example.demo.domain.model.Cartao;
import com.example.demo.domain.model.Pagamento;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PagamentoRepository extends BaseRepository<Pagamento, Long> {
    Optional<Pagamento> findByUuid(UUID uuid);
}

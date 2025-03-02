package com.example.demo.repository;

import com.example.demo.domain.model.Produto;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProdutoRepository extends BaseRepository<Produto, Long> {
    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);
    Optional<Produto> findByUuid(UUID uuid);

}

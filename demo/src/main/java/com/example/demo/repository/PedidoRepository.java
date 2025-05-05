package com.example.demo.repository;

import com.example.demo.domain.model.Pedido;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PedidoRepository extends BaseRepository<Pedido, Long> {
    <T> Optional<T> findByUuid(UUID uuid, Class<T> projectionClass);

    Optional<Pedido> findByUuid(UUID uuid);

}

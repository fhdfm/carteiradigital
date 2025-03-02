package com.example.demo.repository;

import com.example.demo.domain.model.Categoria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends BaseRepository<Categoria, Long> {

    Optional<Categoria> findByUuid(UUID uuid);
    Page<Categoria> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}

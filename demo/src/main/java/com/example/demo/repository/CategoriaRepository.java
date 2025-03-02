package com.example.demo.repository;

import com.example.demo.domain.model.CategoriaProduto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoriaRepository extends BaseRepository<CategoriaProduto, Long> {

    Optional<CategoriaProduto> findByUuid(UUID uuid);
    Page<CategoriaProduto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

}

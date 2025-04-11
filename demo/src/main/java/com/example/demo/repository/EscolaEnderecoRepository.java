package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.EscolaEndereco;

@Repository
public interface EscolaEnderecoRepository extends JpaRepository<EscolaEndereco, Long> {
    
    Optional<EscolaEndereco> findByEscola_Uuid(UUID escolaId);

    boolean existsByEscola_Uuid(UUID escolaId);    
}

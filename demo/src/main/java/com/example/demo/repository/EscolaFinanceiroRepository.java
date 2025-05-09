package com.example.demo.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.domain.model.EscolaFinanceiro;

@Repository
public interface EscolaFinanceiroRepository extends JpaRepository<EscolaFinanceiro, Long> {
    
    Optional<EscolaFinanceiro> findByEscola_Uuid(UUID escolaId);
}

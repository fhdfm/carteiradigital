package com.example.demo.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Status;
import com.example.demo.repositories.EscolaRepository;

@Service
public class EscolaService {
    
    private EscolaRepository repository;

    public EscolaService(EscolaRepository repository) {
        this.repository = repository;
    }

    public void salvar(Escola escola) {

        if (Objects.isNull(escola.getNome()))
            throw new IllegalArgumentException("Nome deve ser preenchido.");

        repository.save(escola);
    }

    public void salvar(UUID uuid, Escola escola) {

        if (Objects.isNull(escola.getNome()))
            throw new IllegalArgumentException("Nome deve ser preenchido.");

        if (Objects.isNull(escola.getStatus()))
            throw new IllegalArgumentException("Status deve ser preenchido.");

        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));

        escolaDb.setNome(escola.getNome());
        escolaDb.setStatus(escola.getStatus());

        repository.save(escolaDb);
    }

    public Escola buscarPorUuid(UUID uuid) {

        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));

        return escolaDb;

    }

    public Page<Escola> listar(Pageable pageable) {
        return repository.findByStatus(pageable, Status.ATIVO);
    }

}

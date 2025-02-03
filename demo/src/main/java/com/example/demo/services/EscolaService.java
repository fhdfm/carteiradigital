package com.example.demo.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Status;
import com.example.demo.repositories.EscolaRepository;
import com.example.demo.util.CnpjChecker;

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

        CnpjChecker cnpjChecker = new CnpjChecker(escola.getCnpj());

        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));

        escolaDb.setNome(escola.getNome());
        escolaDb.setCnpj(cnpjChecker.parse());

        repository.save(escolaDb);
    }

    public Escola buscarPorUuid(UUID uuid) {

        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));

        CnpjChecker cnpjChecker = new CnpjChecker(escolaDb.getCnpj());
        escolaDb.setCnpj(cnpjChecker.format());

        return escolaDb;

    }

    public Page<Escola> listar(EscolaSpecification specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    public void inativar(UUID uuid) {
        
        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));
        escolaDb.setStatus(Status.INATIVO);
        
        repository.save(escolaDb);
    }

    public void ativar(UUID uuid) {
        
        Escola escolaDb = repository.findByUuid(uuid)
            .orElseThrow(() -> new IllegalArgumentException("Escola não encontrada."));
        escolaDb.setStatus(Status.ATIVO);
        
        repository.save(escolaDb);
    }

}

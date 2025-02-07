package com.example.demo.services;

import java.util.Objects;
import java.util.UUID;

import com.example.demo.config.api.exception.EscolaException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.EscolaView;
import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Status;
import com.example.demo.repositories.EscolaRepository;
import com.example.demo.util.CnpjChecker;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EscolaService {
    
    private final EscolaRepository repository;

    public EscolaService(EscolaRepository repository) {
        this.repository = repository;
    }

    private void validate(EscolaRequest request) {

        if (Objects.isNull(request.nome()) || request.nome().length() < 5)
            throw EscolaException.ofValidation("Nome deve ser preenchido.");

        if (Objects.isNull(request.cnpj()) || request.cnpj().length() < 14)
            throw EscolaException.ofValidation("CNPJ deve ser preenchido.");

    }

    public void salvar(EscolaRequest request) {

        validate(request);

        CnpjChecker cnpjChecker = new CnpjChecker(request.cnpj());
        String cnpj = cnpjChecker.parse();

        String nome = request.nome();

        Escola escola = repository.findByCnpj(
                cnpj).orElse(null);

        if (Objects.nonNull(escola))
            throw EscolaException.ofValidation("CNPJ já cadastrado.");

        escola = new Escola();
        escola.setNome(nome);
        escola.setCnpj(cnpj);
        escola.setStatus(Status.ATIVO);

        repository.save(escola);
    }

    public void salvar(UUID uuid, EscolaRequest request) {

        validate(request);

        CnpjChecker cnpjChecker = new CnpjChecker(request.cnpj());
        String cnpj = cnpjChecker.parse();

        String nome = request.nome();

        Escola escola = repository.findByCnpj(
            cnpj).orElse(null);

        if (Objects.nonNull(escola) && !uuid.equals(escola.getUuid()))
                throw EscolaException.ofConflict("CNPJ já cadastrado.");

        escola.setNome(nome);
        escola.setCnpj(cnpj);

        repository.save(escola);
    }

    public EscolaView buscarPorUuid(UUID uuid) {

        Escola escola = repository.findByUuid(uuid)
            .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));

        CnpjChecker cnpjChecker = new CnpjChecker(escola.getCnpj());
        
        return new EscolaView(
            uuid, escola.getNome(), cnpjChecker.format(), escola.getStatus());
    }

    public Page<EscolaView> listar(EscolaSpecification specification, Pageable pageable) {
//        return repository.findAll(specification, pageable, EscolaView.class);

        // Gambiarra pra funcionar enquanto n resolvemos o <T> Page<T> findAll(EscolaSpecification specification, Pageable pageable, Class<T> clazz);
        return repository
                .findAll(specification, pageable)
                .map(escola -> new EscolaView(escola.getUuid(), escola.getNome(), escola.getCnpj(), escola.getStatus()));
    }

    public void inativar(UUID uuid) {
        
        Escola escola = repository.findByUuid(uuid)
            .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));
        escola.setStatus(Status.INATIVO);
        
        repository.save(escola);
    }

    public void ativar(UUID uuid) {
        
        Escola escola = repository.findByUuid(uuid)
            .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));
            escola.setStatus(Status.ATIVO);
        
        repository.save(escola);
    }

}

package com.example.demo.service;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.projection.EscolaView;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.specification.EscolaSpecification;

import jakarta.persistence.EntityNotFoundException;

@Service
public class EscolaService {
    
    private EscolaRepository repository;

    public EscolaService(EscolaRepository repository) {
        this.repository = repository;
    }

    public void salvar(EscolaRequest request) {

        String nome = request.nome();
        String cnpj = request.cnpj();

        Escola escola = repository.findByCnpj(
                cnpj).orElse(null);

        if (Objects.nonNull(escola))
            throw new IllegalArgumentException("CNPJ já cadastrado.");

        escola = new Escola();
        escola.setNome(nome);
        escola.setCnpj(cnpj);
        escola.setStatus(Status.ATIVO);

        repository.save(escola);
        //repository.flush();

        //System.out.println(escola.getUuid());
    }

    public void salvar(UUID uuid, EscolaRequest request) {

        String cnpj = request.cnpj();
        String nome = request.nome();

        Escola escola = repository.findByCnpj(
            cnpj).orElse(null);

        if (Objects.nonNull(escola))
            if (!uuid.equals(escola.getUuid()))
                throw new IllegalArgumentException("CNPJ já cadastrado.");

        escola.setNome(nome);
        escola.setCnpj(cnpj);

        repository.save(escola);
    }

    public EscolaView buscarPorUuid(UUID uuid) {

        return repository.findByUuid(uuid, EscolaView.class)
            .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));
    }

    public Page<EscolaView> listar(EscolaSpecification specification, Pageable pageable) {
        return repository.findAllProjected(specification, pageable, EscolaView.class);
    }

    public void inativar(UUID uuid) {
        
        Escola escola = repository.findByUuid(uuid)
            .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));
        escola.setStatus(Status.INATIVO);
        
        repository.save(escola);
    }

    public void ativar(UUID uuid) {
        
        Escola escola = repository.findByUuid(uuid)
            .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));
            escola.setStatus(Status.ATIVO);
        
        repository.save(escola);
    }

}

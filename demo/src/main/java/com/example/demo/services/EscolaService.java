package com.example.demo.services;

import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.EscolaView;
import com.example.demo.entity.Escola;
import com.example.demo.entity.enums.Perfil;
import com.example.demo.entity.enums.Status;
import com.example.demo.repositories.EscolaRepository;
import com.example.demo.security.UsuarioLogado;

import jakarta.persistence.EntityNotFoundException;
import jakarta.ws.rs.ForbiddenException;

@Service
public class EscolaService extends BaseService {
    
    private EscolaRepository repository;

    public EscolaService(EscolaRepository repository) {
        this.repository = repository;
    }

    private void validate(EscolaRequest request) {

        if (Objects.isNull(request.nome()) || request.nome().length() < 5)
            throw new IllegalArgumentException("Nome deve ser preenchido.");

        if (Objects.isNull(request.cnpj()) || request.cnpj().length() < 14)
            throw new IllegalArgumentException("CNPJ deve ser preenchido.");

    }

    public void salvar(EscolaRequest request) {

        validate(request);

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
        repository.flush();

        System.out.println(escola.getUuid());
    }

    public void salvar(UUID uuid, EscolaRequest request) {

        validate(request);

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

        UsuarioLogado usuarioLogado = getUsuarioLogado();
        if (usuarioLogado.possuiPerfil(Perfil.ADMIN.name())) {
            if (!uuid.equals(usuarioLogado.getEscolaId()))
                throw new ForbiddenException("Acesso negado.");
        }

        return repository.findByUuid(uuid, EscolaView.class)
            .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));
    }

    // TODO - rever
    // public Escola buscarPorUuidX(UUID uuid) {

    //     return repository.findByUuid(uuid)
    //         .orElseThrow(() -> new EntityNotFoundException("Escola não encontrada."));
    // }

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

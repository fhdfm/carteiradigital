package com.example.demo.service;

import com.example.demo.domain.enums.Perfil;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.*;
import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.escola.EscolaView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.EscolaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EscolaService {

    private final EscolaRepository escolaRepository;
    private final UsuarioService usuarioService;

    public EscolaService(EscolaRepository escolaRepository, UsuarioService usuarioService   ) {
        this.escolaRepository = escolaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public UUID salvar(EscolaCreationRequest request) {

        String nome = request.nome();
        String cnpj = request.cnpj();

        Escola escola = escolaRepository.findByCnpj(
                cnpj).orElse(null);

        if (Objects.nonNull(escola))
            throw EurekaException.ofValidation("CNPJ já cadastrado.");

        escola = new Escola();
        escola.setNome(nome);
        escola.setCnpj(cnpj);
        escola.setStatus(Status.ATIVO);

        escolaRepository.save(escola);

        usuarioService.createUser(
                new UsuarioRequest(
                        escola.getUuid(),
                        request.nomeAdmin(),
                        request.emailAdmin(),
                        request.cpfAdmin(),
                        request.telefoneAdmin(),
                        Perfil.ADMIN
                )
        );

        return escola.getUuid();

    }

    public void salvar(UUID uuid, EscolaRequest request) {

        String cnpj = request.cnpj();
        String nome = request.nome();

        Escola escola = escolaRepository.findByCnpj(
                cnpj).orElse(null);

        if (Objects.nonNull(escola) && !uuid.equals(escola.getUuid()))
            throw EurekaException.ofConflict("CNPJ já cadastrado.");

        escola.setNome(nome);
        escola.setCnpj(cnpj);

        escolaRepository.save(escola);
    }

    public EscolaView buscarPorUuid(UUID uuid) {

        return escolaRepository.findByUuid(uuid, EscolaView.class)
                .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada."));
    }

    public Page<EscolaView> listar(EscolaSpecification specification, Pageable pageable) {
        Page<EscolaView> page = escolaRepository.findAllProjected(specification, pageable, EscolaView.class);

        if (page.isEmpty()) {
            throw EurekaException.ofNoContent("Consulta com filtro informado não possui dados para retorno");
        }

        return page;
    }

    public void inativar(UUID uuid) {

        Escola escola = findByUuid(uuid);

        escola.setStatus(Status.INATIVO);

        escolaRepository.save(escola);
    }

    public void ativar(UUID uuid) {

        Escola escola = findByUuid(uuid);

        escola.setStatus(Status.ATIVO);

        escolaRepository.save(escola);
    }

    public Escola findByUuid(UUID uuid) {
        return escolaRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada."));
    }

    public List<EscolaIdAndName> getCombobox() {
        return escolaRepository.findAllProjected();
    }
}

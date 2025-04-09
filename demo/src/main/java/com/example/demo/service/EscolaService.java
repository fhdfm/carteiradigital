package com.example.demo.service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.EscolaParametrosRequest;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.EscolaUsuariosView;
import com.example.demo.dto.UsuarioView;
import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.escola.EscolaView;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.specification.EscolaSpecification;

@Service
public class EscolaService {

    private final EscolaRepository escolaRepository;
    private final UsuarioRepository usuarioRepository;

    public EscolaService(EscolaRepository escolaRepository, UsuarioRepository usuarioRepository   ) {
        this.escolaRepository = escolaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public UUID salvar(EscolaRequest request) {

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

    public void atualizarParametrosEscola(UUID uuid, EscolaParametrosRequest request) {

        Escola escola = findByUuid(uuid);

        escola.setPaymentSecret(request.paymentSecret());

        escolaRepository.save(escola);
    }

    public Escola findByUuid(UUID uuid) {
        return escolaRepository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada."));
    }

    public List<EscolaIdAndName> getCombobox() {
        return escolaRepository.findAllProjected();
    }

    public EscolaUsuariosView buscarUsuariosEscolaPorUuid(UUID escolaId, Pageable pageable) {
        EscolaView escola = escolaRepository.findEscolaViewByUuid(escolaId)
                .orElseThrow(() -> EurekaException.ofNotFound("Escola não encontrada"));

        Page<UsuarioView> usuarios = usuarioRepository.findAllByEscolaUuid(escolaId, pageable);

        return new EscolaUsuariosView(escola, usuarios);
    }
}

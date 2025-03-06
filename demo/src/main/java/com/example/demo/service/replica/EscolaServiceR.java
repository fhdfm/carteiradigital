package com.example.demo.service.replica;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Escola;
import com.example.demo.dto.EscolaParametrosRequest;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.EscolaUsuariosView;
import com.example.demo.dto.UsuarioView;
import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.escola.EscolaView;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.EscolaRepository;
import com.example.demo.repository.specification.EscolaSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class EscolaServiceR {

    private final EscolaRepository repository;
    private final EscolaUsuarioService escolaUsuarioService;

    public EscolaServiceR(
            EscolaRepository repository,
            EscolaUsuarioService escolaUsuarioService
    ) {
        this.repository = repository;
        this.escolaUsuarioService = escolaUsuarioService;
    }

    public void salvar(EscolaRequest request) {

        String nome = request.nome();
        String cnpj = request.cnpj();

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

        String cnpj = request.cnpj();
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

        return repository.findByUuid(uuid, EscolaView.class)
                .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));
    }

    public Page<EscolaView> listar(EscolaSpecification specification, Pageable pageable) {
        Page<EscolaView> page = repository.findAllProjected(specification, pageable, EscolaView.class);

        if (page.isEmpty()) {
            throw EscolaException.ofNoContent("Consulta com filtro informado não possui dados para retorno");
        }

        return page;
    }

    public void inativar(UUID uuid) {

        Escola escola = findByUuid(uuid);

        escola.setStatus(Status.INATIVO);

        repository.save(escola);
    }

    public void ativar(UUID uuid) {

        Escola escola = findByUuid(uuid);

        escola.setStatus(Status.ATIVO);

        repository.save(escola);
    }

    public void atualizarParametrosEscola(UUID uuid, EscolaParametrosRequest request) {

        Escola escola = findByUuid(uuid);

        escola.setPaymentSecret(request.paymentSecret());

        repository.save(escola);
    }

    public Escola findByUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada."));
    }

    public List<EscolaIdAndName> getCombobox() {
        return repository.findAllProjected();
    }

    public EscolaUsuariosView buscarUsuariosEscolaPorUuid(UUID escolaId, Pageable pageable) {
        EscolaView escola = repository.findEscolaViewByUuid(escolaId)
                .orElseThrow(() -> EscolaException.ofNotFound("Escola não encontrada"));

        Page<UsuarioView> usuarios = escolaUsuarioService.findAllByEscolaUuid(escolaId, pageable);

        return new EscolaUsuariosView(escola, usuarios);
    }
}

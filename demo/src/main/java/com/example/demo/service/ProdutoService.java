package com.example.demo.service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Produto;
import com.example.demo.dto.ProdutoRequest;
import com.example.demo.dto.projection.ProdutoView;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.repository.specification.ProdutoSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public void salvar(ProdutoRequest request) {
        Produto produto = new Produto();

        produto.setNome(request.nome());
        produto.getCategoria().setUuid(request.categoriaId());
        produto.setStatus(Status.ATIVO);

        repository.save(produto);
    }

    public void salvar(UUID uuid, ProdutoRequest request) {
        Produto produto = new Produto();

        if (uuid != null) {
            produto = repository.findByUuid(uuid)
                    .orElseThrow(() -> EscolaException.ofNotFound("Produto não encontrado."));
        }

        produto.setNome(request.nome());
        produto.setPreco(request.preco());
        produto.getCategoria().setUuid(request.categoriaId());

        repository.save(produto);
    }

    public ProdutoView buscarPorUuid(UUID uuid) {
        return repository.findByUuid(uuid, ProdutoView.class)
                .orElseThrow(() -> EscolaException.ofNotFound("Produto não encontrado."));
    }

    public Page<ProdutoView> listar(ProdutoSpecification specification, Pageable pageable) {
        Page<ProdutoView> page = repository.findAllProjected(specification, pageable, ProdutoView.class);

        if (page.isEmpty()) {
            EscolaException.ofNoContent("Consulta com filtro informado não possui dados para retorno");
        }

        return page;
    }

    public void modificarStatus(UUID uuid) {
        Optional<Produto> produto = repository.findByUuid(uuid);
        produto.get().setStatus(produto.get().getStatus() == Status.ATIVO ? Status.ATIVO : Status.INATIVO);
        repository.save(produto.get());
    }

}

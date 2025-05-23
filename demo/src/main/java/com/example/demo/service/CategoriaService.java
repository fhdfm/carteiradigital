package com.example.demo.service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.CategoriaProduto;
import com.example.demo.dto.CategoriaRequest;
import com.example.demo.dto.projection.ProdutoView;
import com.example.demo.dto.projection.produto.CategoriaSummary;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.repository.ProdutoRepository;
import com.example.demo.repository.specification.CategoriaProdutoSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;


    public CategoriaService(CategoriaRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    public void salvar(UUID uuid, CategoriaRequest categoriaRequest) {
        CategoriaProduto categoria = new CategoriaProduto();
        if (uuid != null) {
            categoria = repository.findByUuid(uuid)
                    .orElseThrow(() -> EurekaException.ofNotFound("Categoria não encontrado."));
        }
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();

        categoria.setNome(categoriaRequest.nome());
        categoria.getEscola().setId(usuarioLogado.getEscola().getId());
        categoria.setStatus(Status.ATIVO);

        repository.save(categoria);
    }


    public CategoriaProduto buscarPorUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Categoria não encontrado."));
    }

    public Page<CategoriaSummary> listar(CategoriaProdutoSpecification specification, Pageable pageable) {
        return this.repository.findAllProjected(specification, pageable, CategoriaSummary.class);
    }

    public void modificarStatus(UUID uuid) {
        Optional<CategoriaProduto> produto = repository.findByUuid(uuid);
        if (produto.isPresent()) {
            produto.get().setStatus(produto.get().getStatus() == Status.ATIVO ? Status.INATIVO : Status.ATIVO);
            repository.save(produto.get());
        }
    }

    public List<ProdutoView> buscarListaDeProdutosPorCategoriaUuid(UUID uuid) {
        return produtoRepository.findByCategoria_Uuid(uuid, ProdutoView.class);
    }
}

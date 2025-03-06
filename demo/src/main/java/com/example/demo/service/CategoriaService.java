package com.example.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.CategoriaProduto;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.CategoriaRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public void salvar(UUID uuid, String nome) {
        CategoriaProduto categoria = new CategoriaProduto();
        if (uuid != null) {
            categoria = repository.findByUuid(uuid)
                    .orElseThrow(() -> EurekaException.ofNotFound("Categoria não encontrado."));
        }
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();

        categoria.setNome(nome);
        categoria.getEscola().setId(usuarioLogado.getEscola().getId());
        categoria.setStatus(Status.ATIVO);

        repository.save(categoria);
    }


    public CategoriaProduto buscarPorUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> EurekaException.ofNotFound("Categoria não encontrado."));
    }

    public Page<CategoriaProduto> listar(String nome, Pageable pageable) {
        Page<CategoriaProduto> page;

        if (nome != null && !nome.isBlank()) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        if (page.isEmpty()) {
            throw EurekaException.ofNoContent("Nenhuma categoria encontrada.");
        }

        return page;
    }

    public void modificarStatus(UUID uuid) {
        Optional<CategoriaProduto> produto = repository.findByUuid(uuid);
        produto.get().setStatus(produto.get().getStatus() == Status.ATIVO ? Status.INATIVO : Status.ATIVO);
        repository.save(produto.get());
    }

}

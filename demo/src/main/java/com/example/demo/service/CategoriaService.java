package com.example.demo.service;

import com.example.demo.domain.enums.Status;
import com.example.demo.domain.model.Categoria;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.CategoriaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    public void salvar(UUID uuid, String nome) {
        Categoria categoria = new Categoria();
        if (uuid != null) {
            categoria = repository.findByUuid(uuid)
                    .orElseThrow(() -> EscolaException.ofNotFound("Categoria não encontrado."));
        }
        categoria.setNome(nome);
        categoria.setStatus(Status.ATIVO);

        repository.save(categoria);
    }


    public Categoria buscarPorUuid(UUID uuid) {
        return repository.findByUuid(uuid)
                .orElseThrow(() -> EscolaException.ofNotFound("Categoria não encontrado."));
    }

    public Page<Categoria> listar(String nome, Pageable pageable) {
        Page<Categoria> page;

        if (nome != null && !nome.isBlank()) {
            page = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        if (page.isEmpty()) {
            throw EscolaException.ofNoContent("Nenhuma categoria encontrada.");
        }

        return page;
    }

    public void modificarStatus(UUID uuid) {
        Optional<Categoria> produto = repository.findByUuid(uuid);
        produto.get().setStatus(produto.get().getStatus() == Status.ATIVO ? Status.ATIVO : Status.INATIVO);
        repository.save(produto.get());
    }

}

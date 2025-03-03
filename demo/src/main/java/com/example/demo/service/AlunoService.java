package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.domain.model.Usuario;
import com.example.demo.repository.AlunoRepository;

@Service
public class AlunoService {

    private final AlunoRepository repository;

    public AlunoService(AlunoRepository repository) {
        this.repository = repository;
    }

    public boolean existsByResponsavel(Usuario responsavel) {
        return repository.existsByResponsavel(responsavel);
    }

}

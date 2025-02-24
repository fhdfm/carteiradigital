package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.repository.AlunoRepository;

@Service
public class AlunoService {
    
    private final AlunoRepository repository;
    private final UsuarioService usuarioService;

    public AlunoService(AlunoRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }

    public boolean isAlunoDoResponsavel(UUID alunoId, UUID responsavelId) {
        return repository.isAlunoDoResponsavel(alunoId, responsavelId);
    }
    
}

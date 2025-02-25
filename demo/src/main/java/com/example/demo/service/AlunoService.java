package com.example.demo.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AlunoService {
    
    public boolean isAlunoDoResponsavel(UUID alunoId, UUID responsavelId) {
        return true;
    }
    
}

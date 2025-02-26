package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.validation.annotation.CPF;

public record UsuarioRequest(UUID escola, 
        String nome, String email, String senha, 
        @CPF String cpf, String telefone, 
        MetodoAutenticacao metodoAutenticacao, Perfil perfil) {
    
}

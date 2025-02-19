package com.example.demo.services;

import org.springframework.security.core.context.SecurityContextHolder;

import com.example.demo.security.UsuarioLogado;

public class BaseService {
    
    public UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}

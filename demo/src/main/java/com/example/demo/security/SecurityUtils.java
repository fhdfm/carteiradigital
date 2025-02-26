package com.example.demo.security;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
    
    private SecurityUtils() {
    }

    public static UsuarioLogado getUsuarioLogado() {
        return (UsuarioLogado) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }

}

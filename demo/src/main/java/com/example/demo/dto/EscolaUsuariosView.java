package com.example.demo.dto;

import com.example.demo.dto.projection.escola.EscolaView;
import org.springframework.data.domain.Page;

public record EscolaUsuariosView(
        EscolaView escola, Page<UsuarioView> usuarios
) {
}

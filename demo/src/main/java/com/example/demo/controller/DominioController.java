package com.example.demo.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.enums.Departamento;
import com.example.demo.domain.enums.MetodoAutenticacao;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.Status;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.util.ApiReturn;

@RestController
@RequestMapping("/api/enums")
public class DominioController {
    
    @GetMapping("/perfil")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<ApiReturn<Perfil[]>> listarPerfis() {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        List<Perfil> perfisParaExibicao = Arrays.stream(Perfil.values())
            .filter(perfil -> podeVisualizar(perfil, currentUser))
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiReturn.of(perfisParaExibicao.toArray(new Perfil[0])));
    }

    private boolean podeVisualizar(Perfil perfil, UsuarioLogado currentUser) {
        
        boolean isMaster = currentUser.possuiPerfil(Perfil.MASTER);
        if (isMaster)
            return true;

        boolean isAdmin = currentUser.possuiPerfil(Perfil.ADMIN);
        if (isAdmin)
            return perfil != Perfil.MASTER;

        boolean isFuncionario = currentUser.possuiPerfil(Perfil.FUNCIONARIO);
        if (isFuncionario)
            return perfil != Perfil.MASTER && perfil != Perfil.ADMIN && perfil != Perfil.FUNCIONARIO;

        return false;
    }

    @GetMapping("/departamento")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO', 'PDV')")
    public ResponseEntity<ApiReturn<Departamento[]>> listarDepartamentos() {
        return ResponseEntity.ok(ApiReturn.of(Departamento.values()));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO', 'PDV', 'RESPONSAVEL', 'ALUNO')")
    public ResponseEntity<ApiReturn<Status[]>> listarStatus() {
        return ResponseEntity.ok(ApiReturn.of(Status.values()));
    }

    @GetMapping("/metodo-autenticacao")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO')")
    public ResponseEntity<ApiReturn<MetodoAutenticacao[]>> listarMetodosAutenticacao() {
        return ResponseEntity.ok(ApiReturn.of(MetodoAutenticacao.values()));
    }

}

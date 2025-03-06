package com.example.demo.controller;

import java.util.Arrays;

import com.example.demo.controller.doc.EurekaApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Domínio", description = "Endpoints para gerenciamento de domínios")
public class DominioController {
    
    @GetMapping("/perfil")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Lista os perfis",
            description = "Lista os perfis da aplicação"
    )
    public ResponseEntity<ApiReturn<Perfil[]>> listarPerfis() {
        
        Perfil[] perfisParaExibicao = Arrays.stream(Perfil.values())
            .filter(this::podeVisualizar)
            .toArray(Perfil[]::new);

        return ResponseEntity.ok(ApiReturn.of(perfisParaExibicao));
    }

    private boolean podeVisualizar(Perfil perfil) {
        
        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();

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
    @EurekaApiOperation(
            summary = "Lista os departamentos",
            description = "Lista os departamentos da aplicação"
    )
    public ResponseEntity<ApiReturn<Departamento[]>> listarDepartamentos() {
        return ResponseEntity.ok(ApiReturn.of(Departamento.values()));
    }

    @GetMapping("/status")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO', 'PDV', 'RESPONSAVEL', 'ALUNO')")
    @EurekaApiOperation(
            summary = "Lista os status",
            description = "Lista os status da aplicação"
    )
    public ResponseEntity<ApiReturn<Status[]>> listarStatus() {
        return ResponseEntity.ok(ApiReturn.of(Status.values()));
    }

    @GetMapping("/metodo-autenticacao")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Lista os métodos de autenticação",
            description = "Lista os métodos de autenticação da aplicação"
    )
    public ResponseEntity<ApiReturn<MetodoAutenticacao[]>> listarMetodosAutenticacao() {
        return ResponseEntity.ok(ApiReturn.of(MetodoAutenticacao.values()));
    }

}

package com.example.demo.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.enums.Status;
import com.example.demo.dto.CurrentUserView;
import com.example.demo.dto.TrocarSenhaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.projection.usuario.UsuarioFull;
import com.example.demo.dto.projection.usuario.UsuarioSummary;
import com.example.demo.repository.specification.UsuarioSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.service.UsuarioService;
import com.example.demo.util.ApiReturn;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @PostMapping
    public ResponseEntity<ApiReturn<UUID>> create(@RequestBody @Valid UsuarioRequest request) {
        UUID uuid = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(uuid));
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','PDV','RESPONSAVEL')")
    @PutMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.USUARIO)
    public ResponseEntity<ApiReturn<String>> update(@PathVariable("uuid") UUID uuid,
                                                    @RequestBody @Valid UsuarioRequest request) {
        service.updateUser(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Usuário atualizado com sucesso."));
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @GetMapping
    public ResponseEntity<ApiReturn<Page<UsuarioSummary>>> findAll(
            @ParameterObject UsuarioSpecification specification,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.findAllUsers(specification, pageable)));
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','PDV','RESPONSAVEL')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiReturn<UsuarioFull>> findByUuid(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(ApiReturn.of(service.findUserByUuid(uuid, UsuarioFull.class)));
    }

    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO', 'PDV', 'RESPONSAVEL', 'ALUNO')")
    @GetMapping("/current")
    public ResponseEntity<ApiReturn<CurrentUserView>> buscarUsuarioLogado() {
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();
        CurrentUserView view = new CurrentUserView(
                usuarioLogado.getName(),
                usuarioLogado.getUsername(),
                usuarioLogado.getPerfil());

        return ResponseEntity.ok(ApiReturn.of(view));
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<ApiReturn<String>> inativar(@PathVariable("uuid") UUID uuid) {
        service.changeUserStatus(uuid, Status.INATIVO);
        return ResponseEntity.ok(ApiReturn.of("Usuário inativado com sucesso."));
    }

    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<ApiReturn<String>> ativar(@PathVariable("uuid") UUID uuid) {
        service.changeUserStatus(uuid, Status.ATIVO);
        return ResponseEntity.ok(ApiReturn.of("Usuário reativado com sucesso."));
    }    

    @PostMapping("/change-password")
    public ResponseEntity<ApiReturn<String>> changePassword(@RequestBody @Valid TrocarSenhaRequest request) {
        service.changePassword(request);
        return ResponseEntity.ok(ApiReturn.of("Senha alterada com sucesso."));
    }
}

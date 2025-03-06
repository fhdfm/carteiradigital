package com.example.demo.controller;

import java.util.UUID;

import com.example.demo.controller.doc.EurekaApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuários", description = "Endpoints para gerenciamento de usuários")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Criar um usuário",
            description = "Cria e persiste um novo usuário contendo as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<UUID>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um usuário",
                    required = true
            )
            @RequestBody @Valid UsuarioRequest request
    ) {
        UUID uuid = service.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(uuid));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','PDV','RESPONSAVEL')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @EurekaApiOperation(
            summary = "Atualizar um usuário",
            description = "Atualiza, a partir do seu UUID, um usuário persistido com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> update(
            @Parameter(description = "UUID do usuário a ser atualizado", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um usuário",
                    required = true
            )
            @RequestBody @Valid UsuarioRequest request
    ) {
        service.updateUser(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Usuário atualizado com sucesso."));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Lista os usuários",
            description = "Retorna um page contendo usuários de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<UsuarioSummary>>> findAll(
            @ParameterObject UsuarioSpecification specification,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.findAllUsers(specification, pageable)));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','PDV','RESPONSAVEL')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @EurekaApiOperation(
            summary = "Busca um usuário",
            description = "Busca, a partir do seu UUID, um usuário persistido."
    )
    public ResponseEntity<ApiReturn<UsuarioFull>> findByUuid(
            @Parameter(description = "UUID do usuário a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.findUserByUuid(uuid, UsuarioFull.class)));
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN', 'FUNCIONARIO', 'PDV', 'RESPONSAVEL', 'ALUNO')")
    @EurekaApiOperation(
            summary = "Busca o usuário logado",
            description = "Busca, o usuário que está logado."
    )
    public ResponseEntity<ApiReturn<CurrentUserView>> buscarUsuarioLogado() {
        UsuarioLogado usuarioLogado = SecurityUtils.getUsuarioLogado();
        CurrentUserView view = new CurrentUserView(
                usuarioLogado.getName(),
                usuarioLogado.getUsername(),
                usuarioLogado.getPerfil());

        return ResponseEntity.ok(ApiReturn.of(view));
    }

    @PutMapping("/{uuid}/inativar")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @EurekaApiOperation(
            summary = "Inativa um usuário",
            description = "Inativa, a partir do seu UUID, um usuário persistido."
    )
    public ResponseEntity<ApiReturn<String>> inativar(
            @Parameter(description = "UUID do usuário a ser inativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.changeUserStatus(uuid, Status.INATIVO);
        return ResponseEntity.ok(ApiReturn.of("Usuário inativado com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @CheckAccess(entity = EntityNames.USUARIO)
    @EurekaApiOperation(
            summary = "Ativa um usuário",
            description = "Ativa, a partir do seu UUID, um usuário persistido."
    )
    public ResponseEntity<ApiReturn<String>> ativar(
            @Parameter(description = "UUID do usuário a ser ativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.changeUserStatus(uuid, Status.ATIVO);
        return ResponseEntity.ok(ApiReturn.of("Usuário reativado com sucesso."));
    }    

    @PostMapping("/change-password")
    @EurekaApiOperation(
            summary = "Trocar senha",
            description = "Troca a senha de um usuário persistido."
    )
    public ResponseEntity<ApiReturn<String>> changePassword(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de troca de senha",
                    required = true
            )
            @RequestBody @Valid TrocarSenhaRequest request
    ) {
        service.changePassword(request);
        return ResponseEntity.ok(ApiReturn.of("Senha alterada com sucesso."));
    }
}

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

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.domain.enums.Status;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.ResponsavelAlunoRequest;
import com.example.demo.dto.projection.aluno.AlunoFull;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.service.AlunoService;
import com.example.demo.service.ResponsavelAlunoService;
import com.example.demo.util.ApiReturn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Endpoints para gerenciamento de alunos")
public class AlunoController {
    
    private final AlunoService service;
    private final ResponsavelAlunoService responsavelAlunoService;

    public AlunoController(AlunoService service, ResponsavelAlunoService responsavelAlunoService) {
        this.service = service;
        this.responsavelAlunoService = responsavelAlunoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Criar um aluno",
            description = "Cria e persiste um novo aluno contendo as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<UUID>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um aluno",
                    required = true
            )
            @RequestBody @Valid AlunoRequest request
    ) {
        UUID uuid = this.service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(uuid));
    }

    @PutMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','ALUNO')")
    @EurekaApiOperation(
            summary = "Atualizar um aluno",
            description = "Atualiza, a partir do seu UUID, um aluno persistido com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> update(
            @Parameter(description = "UUID do aluno a ser atualizado", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados do aluno",
                    required = true
            )
            @RequestBody @Valid AlunoRequest request
    ) {
        this.service.update(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Aluno atualizado com sucesso."));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Lista os alunos",
            description = "Retorna um page contendo alunos de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<AlunoSummary>>> findAll(
            @ParameterObject AlunoSpecification specification,
            @ParameterObject Pageable pageable
    ) {
        Page<AlunoSummary> list = this.service.findAll(specification, pageable);
        return ResponseEntity.ok(ApiReturn.of(list));
    }

    @GetMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','ALUNO')")
    @EurekaApiOperation(
            summary = "Busca um aluno",
            description = "Busca, a partir do seu UUID, um aluno persistido."
    )
    public ResponseEntity<ApiReturn<AlunoFull>> findByUuid(
            @Parameter(description = "UUID do aluno a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        AlunoFull student = this.service.findByUuid(uuid, AlunoFull.class);
        return ResponseEntity.ok(ApiReturn.of(student));
    }

    @PutMapping("/{uuid}/ativar")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Ativa um aluno",
            description = "Ativa, a partir do seu UUID, um aluno persistido."
    )
    public ResponseEntity<ApiReturn<String>> activate(
            @Parameter(description = "UUID do aluno a ser ativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        this.service.changeStudentStatus(uuid, Status.ATIVO);
        return ResponseEntity.ok(ApiReturn.of("Aluno ativado com sucesso."));
    }

    @PutMapping("/{uuid}/inativar")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Inativa um aluno",
            description = "Inativa, a partir do seu UUID, um aluno persistido."
    )
    public ResponseEntity<ApiReturn<String>> deactivate(
            @Parameter(description = "UUID do aluno a ser inativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        this.service.changeStudentStatus(uuid, Status.INATIVO);
        return ResponseEntity.ok(ApiReturn.of("Aluno inativado com sucesso."));
    }
    
    @PostMapping("/{uuid}/responsavel")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<Long>> addResponsavel(
            @Parameter(description = "UUID do aluno", required = true)
            @PathVariable("uuid") UUID uuid,
            
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados do responsável",
                    required = true
            )
            @RequestBody @Valid ResponsavelAlunoRequest request
    ) {
        Long responsavelId = this.responsavelAlunoService.create(request, uuid);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(responsavelId));
    }
}

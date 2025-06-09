package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.domain.enums.Status;
import com.example.demo.dto.ProfessorRequest;
import com.example.demo.dto.projection.ProfessorFull;
import com.example.demo.dto.projection.ProfessorSummary;
import com.example.demo.repository.specification.ProfessorSpecification;
import com.example.demo.service.ProfessorService;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.util.ApiReturn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/professores")
@Tag(name = "Professores", description = "Endpoints para gerenciamento de professores")
public class ProfessorController {

        private final ProfessorService service;

        public ProfessorController(ProfessorService service) {
            this.service = service;
        }

        @PostMapping
        @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
        @EurekaApiOperation(
                summary = "Criar um professor",
                description = "Cria e persiste um novo professor contendo as informações especificadas na requisião."
        )
        public ResponseEntity<ApiReturn<UUID>> create(
                @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        description = "Corpo da requisição com os dados de um professor",
                        required = true
                )
                @RequestBody @Valid ProfessorRequest request
        ) {
            UUID uuid = this.service.create(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(uuid));
        }

    @PutMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.PROFESSOR)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','PROFESSOR')")
    @EurekaApiOperation(
            summary = "Atualizar um professor",
            description = "Atualiza, a partir do seu UUID, um professor persistido com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> update(
            @Parameter(description = "UUID do professor a ser atualizado", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados do professor",
                    required = true
            )
            @RequestBody @Valid ProfessorRequest request
    ) {
        this.service.update(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Professor atualizado com sucesso."));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Lista os professores",
            description = "Retorna um page contendo professores de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<ProfessorSummary>>> findAll(
            @ParameterObject ProfessorSpecification specification,
            @ParameterObject Pageable pageable
    ) {
        Page<ProfessorSummary> list = this.service.findAll(specification, pageable);
        return ResponseEntity.ok(ApiReturn.of(list));
    }

    @PutMapping("/{uuid}/ativar")
    @CheckAccess(entity = EntityNames.PROFESSOR)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Ativa um professor",
            description = "Ativa, a partir do seu UUID, um professor persistido."
    )
    public ResponseEntity<ApiReturn<String>> activate(
            @Parameter(description = "UUID do professor a ser ativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        this.service.changeStudentStatus(uuid, Status.ATIVO);
        return ResponseEntity.ok(ApiReturn.of("Professor ativado com sucesso."));
    }

    @PutMapping("/{uuid}/inativar")
    @CheckAccess(entity = EntityNames.PROFESSOR)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Inativa um professor",
            description = "Inativa, a partir do seu UUID, um professor persistido."
    )
    public ResponseEntity<ApiReturn<String>> deactivate(
            @Parameter(description = "UUID do professor a ser inativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        this.service.changeStudentStatus(uuid, Status.INATIVO);
        return ResponseEntity.ok(ApiReturn.of("Professor inativado com sucesso."));
    }

    @GetMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.PROFESSOR)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','PROFESSOR')")
    @EurekaApiOperation(
            summary = "Busca um professor",
            description = "Busca, a partir do seu UUID, um professor persistido."
    )
    public ResponseEntity<ApiReturn<ProfessorFull>> findByUuid(
            @Parameter(description = "UUID do professor a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        ProfessorFull student = this.service.findByUuid(uuid, ProfessorFull.class);
        return ResponseEntity.ok(ApiReturn.of(student));
    }
}

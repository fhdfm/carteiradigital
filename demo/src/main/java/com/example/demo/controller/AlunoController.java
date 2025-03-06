package com.example.demo.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.enums.Status;
import com.example.demo.dto.AlunoRequest;
import com.example.demo.dto.projection.aluno.AlunoFull;
import com.example.demo.dto.projection.aluno.AlunoSummary;
import com.example.demo.repository.specification.AlunoSpecification;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.service.UsuarioService;
import com.example.demo.util.ApiReturn;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {
    
    private final UsuarioService service;

    public AlunoController(UsuarioService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<UUID>> create(@RequestBody @Valid AlunoRequest request) {
        UUID uuid = this.service.createStudent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(uuid));
    }

    @PutMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','ALUNO')")
    public ResponseEntity<ApiReturn<String>> update(@PathVariable("uuid") UUID uuid, @RequestBody @Valid AlunoRequest request) {
        this.service.updateStudent(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Aluno atualizado com sucesso."));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<Page<AlunoSummary>>> findAll(AlunoSpecification specification, Pageable pageable) {
        Page<AlunoSummary> list = this.service.findAllStudents(specification, pageable);
        return ResponseEntity.ok(ApiReturn.of(list));
    }

    @GetMapping("/{uuid}")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO','RESPONSAVEL','ALUNO')")
    public ResponseEntity<ApiReturn<AlunoFull>> findByUuid(@PathVariable("uuid") UUID uuid) {
        AlunoFull student = this.service.findStudentByUuid(uuid, AlunoFull.class);
        return ResponseEntity.ok(ApiReturn.of(student));
    }

    @PutMapping("/{uuid}/ativar")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<String>> activate(@PathVariable("uuid") UUID uuid) {
        this.service.changeStudentStatus(uuid, Status.ATIVO);
        return ResponseEntity.ok(ApiReturn.of("Aluno ativado com sucesso."));
    }

    @PutMapping("/{uuid}/inativar")
    @CheckAccess(entity = EntityNames.ALUNO)
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<String>> deactivate(@PathVariable("uuid") UUID uuid) {
        this.service.changeStudentStatus(uuid, Status.INATIVO);
        return ResponseEntity.ok(ApiReturn.of("Aluno inativado com sucesso."));
    }    
}

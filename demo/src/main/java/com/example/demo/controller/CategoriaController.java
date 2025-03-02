package com.example.demo.controller;

import com.example.demo.domain.model.Categoria;
import com.example.demo.service.CategoriaService;
import com.example.demo.util.ApiReturn;
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
@RequestMapping("/api/categoria")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ApiReturn<String>> criarCategoria(@RequestBody @Valid String request) {
        service.salvar(null, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of("Categoria criado com sucesso."));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiReturn<String>> atualizarCategoria(@PathVariable("uuid") UUID uuid,
                                                                @RequestBody @Valid String nome) {
        service.salvar(uuid, nome);
        return ResponseEntity.ok(ApiReturn.of("Categoria atualizado com sucesso."));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiReturn<Categoria>> buscarCategoriaPorUuid(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @GetMapping
    public ResponseEntity<ApiReturn<Page<Categoria>>> listarCategorias(@ParameterObject String nome,
                                                                       @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(nome, pageable)));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<ApiReturn<String>> inativarCategoria(@PathVariable("uuid") UUID uuid) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Categoria inativado com sucesso."));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<ApiReturn<String>> ativarCategoria(@PathVariable("uuid") UUID uuid) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Categoria ativado com sucesso."));
    }
}

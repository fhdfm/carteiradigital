package com.example.demo.controller;

import com.example.demo.dto.ProdutoRequest;
import com.example.demo.dto.projection.ProdutoView;
import com.example.demo.repository.specification.ProdutoSpecification;
import com.example.demo.service.ProdutoService;
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
@RequestMapping("/api/produtos")
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<ApiReturn<String>> criarProduto(@RequestBody @Valid ProdutoRequest request) {
        service.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of("Produto criado com sucesso."));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}")
    public ResponseEntity<ApiReturn<String>> atualizarProduto(@PathVariable("uuid") UUID uuid,
                                                              @RequestBody @Valid ProdutoRequest request) {
        service.salvar(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Produto atualizado com sucesso."));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiReturn<ProdutoView>> buscarProdutoPorUuid(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @GetMapping
    public ResponseEntity<ApiReturn<Page<ProdutoView>>> listarProdutos(@ParameterObject ProdutoSpecification specification,
                                                                       @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(specification, pageable)));
    }

    @PreAuthorize("!hasRole('ALUNO')")
    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<ApiReturn<String>> inativarProduto(@PathVariable("uuid") UUID uuid) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Produto inativado com sucesso."));
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<ApiReturn<String>> ativarProduto(@PathVariable("uuid") UUID uuid) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Produto ativado com sucesso."));
    }
}

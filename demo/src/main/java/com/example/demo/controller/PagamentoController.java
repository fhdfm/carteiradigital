package com.example.demo.controller;

import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.service.pagamento.PagamentoService;
import com.example.demo.util.ApiReturn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/aluno/pagamento", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Pagamento", description = "Endpoints para pagamentos")
public class PagamentoController {

    private final PagamentoService service;

    public PagamentoController(PagamentoService service) {
        this.service = service;
    }
    @PostMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'RESPONSAVEL')")
    public ResponseEntity<ApiReturn<String>> registrarPreCompra(
            @RequestBody List<CriarPagamentoRequest> produto
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.registrarPreCompra(produto)));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'RESPONSAVEL')")
    public ResponseEntity<ApiReturn<String>> confirmarCompra(
            @Parameter(description = "UUID do pagamento a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.confirmarCompra(uuid)));
    }

}

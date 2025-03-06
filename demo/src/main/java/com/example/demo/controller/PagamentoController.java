package com.example.demo.controller;

import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.service.PagamentoService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public ResponseEntity<String> registrarPreCompra(
            @RequestBody List<CriarPagamentoRequest> produto
    ) {
        return ResponseEntity.ok(service.registrarPreCompra(produto));
    }

}

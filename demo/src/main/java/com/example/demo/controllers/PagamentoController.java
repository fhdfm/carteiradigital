package com.example.demo.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/aluno/pagamento", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
public class PagamentoController {
//
//    @Value("${integracao.mercadopago.access-token}")
//    private String ACCESS_TOKEN;
//    private final PagamentoService service;
//
//    public PagamentoController(PagamentoService service) {
//        this.service = service;
//        MercadoPagoConfig.setAccessToken(ACCESS_TOKEN);
//    }
//
//    @PostMapping
//    @PreAuthorize("hasAuthority('SCOPE_ROLE_ALUNO') or hasAuthority('SCOPE_ROLE_EXTERNO')")
//    public ResponseEntity<String> createPayment(
//            @RequestBody ProdutoMercadoPagoRequest produto
//    ) {
//        return ResponseEntity.ok(service.createPayment(produto));
//    }

}

package com.example.demo.controllers;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Escola;
import com.example.demo.services.EscolaService;

@RestController
@RequestMapping("/api/escolas")
public class EscolaController {

    private final EscolaService escolaService;

    public EscolaController(EscolaService escolaService) {
        this.escolaService = escolaService;
    }

    /**
     * Cria uma nova escola.
     * Exemplo de requisição: POST /api/escolas
     * Corpo da requisição (JSON):
     * {
     *   "nome": "Escola Exemplo",
     *   "status": "ATIVO",
     *   ...
     * }
     */
    @PostMapping
    public ResponseEntity<Void> criarEscola(@RequestBody Escola escola) {
        escolaService.salvar(escola);
        return ResponseEntity.ok().build();
    }

    /**
     * Atualiza os dados de uma escola existente identificada pelo UUID.
     * Exemplo de requisição: PUT /api/escolas/{uuid}
     * Corpo da requisição (JSON):
     * {
     *   "nome": "Escola Atualizada",
     *   "status": "ATIVO",
     *   ...
     * }
     */
    @PutMapping("/{uuid}")
    public ResponseEntity<Void> atualizarEscola(@PathVariable("uuid") UUID uuid,
                                                @RequestBody Escola escola) {
        escolaService.salvar(uuid, escola);
        return ResponseEntity.ok().build();
    }

    /**
     * Retorna uma escola pelo UUID.
     * Exemplo de requisição: GET /api/escolas/{uuid}
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<Escola> buscarEscolaPorUuid(@PathVariable("uuid") UUID uuid) {
        Escola escola = escolaService.buscarPorUuid(uuid);
        return ResponseEntity.ok(escola);
    }

    /**
     * Lista as escolas ativas com paginação.
     * Exemplo de requisição: GET /api/escolas?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<Page<Escola>> listarEscolas(Pageable pageable) {
        Page<Escola> escolas = escolaService.listar(pageable);
        return ResponseEntity.ok(escolas);
    }
}

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.projection.EscolaView;
import com.example.demo.repository.specification.EscolaSpecification;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.service.EscolaService;

@RestController
@RequestMapping("/api/escolas")
public class EscolaController {

    private final EscolaService service;

    public EscolaController(EscolaService service) {
        this.service = service;
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
    @PreAuthorize("hasRole('MASTER')")
    @PostMapping
    public ResponseEntity<String> criarEscola(@RequestBody EscolaRequest request) {
        service.salvar(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Escola criada com sucesso.");
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
    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}")
    public ResponseEntity<String> atualizarEscola(@PathVariable("uuid") UUID uuid,
                                                @RequestBody EscolaRequest request) {
        service.salvar(uuid, request);
        return ResponseEntity.ok("Escola atualizada com sucesso.");
    }

    /**
     * Retorna uma escola pelo UUID.
     * Exemplo de requisição: GET /api/escolas/{uuid}
     */
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    @CheckAccess(entity = EntityNames.ESCOLA)
    @GetMapping("/{uuid}")
    public ResponseEntity<EscolaView> buscarEscolaPorUuid(@PathVariable("uuid") UUID uuid) {
        EscolaView escola = service.buscarPorUuid(uuid);
        return ResponseEntity.ok(escola);
    }

    /**
     * Lista as escolas ativas com paginação.
     * Exemplo de requisição: GET /api/escolas?page=0&size=10
     */
    @PreAuthorize("hasRole('MASTER')")
    @GetMapping
    public ResponseEntity<Page<EscolaView>> listarEscolas(EscolaSpecification specification, Pageable pageable) {
        Page<EscolaView> escolas = service.listar(specification, pageable);
        return ResponseEntity.ok(escolas);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<String> inativarEscola(@PathVariable("uuid") UUID uuid) {
        service.inativar(uuid);
        return ResponseEntity.ok("Escola inativada com sucesso.");
    }

    @PreAuthorize("hasRole('MASTER')")
    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<String> ativarEscola(@PathVariable("uuid") UUID uuid) {
        service.ativar(uuid);
        return ResponseEntity.ok("Escola ativada com sucesso.");
    }    

}

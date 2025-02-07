package com.example.demo.controllers;

import java.util.UUID;

import com.example.demo.config.api.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controllers.spec.EscolaSpecification;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.EscolaView;
import com.example.demo.services.EscolaService;

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
    @PostMapping
    public ResponseEntity<ApiResponse<String>> criarEscola(@RequestBody EscolaRequest request) {
        
        service.salvar(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.of("Escola criada com sucesso."));
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
    public ResponseEntity<ApiResponse<String>> atualizarEscola(@PathVariable("uuid") UUID uuid,
                                                               @RequestBody EscolaRequest request) {
        service.salvar(uuid, request);
        
        return ResponseEntity.ok(ApiResponse.of("Escola atualizada com sucesso."));
    }

    /**
     * Retorna uma escola pelo UUID.
     * Exemplo de requisição: GET /api/escolas/{uuid}
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiResponse<EscolaView>> buscarEscolaPorUuid(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(ApiResponse.of(service.buscarPorUuid(uuid)));
    }

    /**
     * Lista as escolas ativas com paginação.
     * Exemplo de requisição: GET /api/escolas?page=0&size=10
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EscolaView>>> listarEscolas(EscolaSpecification specification, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(service.listar(specification, pageable)));
    }

    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<ApiResponse<String>> inativarEscola(@PathVariable("uuid") UUID uuid) {
        
        service.inativar(uuid);
        
        return ResponseEntity.ok(ApiResponse.of("Escola inativada com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<ApiResponse<String>> ativarEscola(@PathVariable("uuid") UUID uuid) {
        service.ativar(uuid);
        return ResponseEntity.ok(ApiResponse.of("Escola ativada com sucesso."));
    }    

}

package com.example.demo.controllers;

import java.util.UUID;

import com.example.demo.config.api.response.ApiReturn;
import com.example.demo.config.api.swagger.EscolaApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
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
@Tag(name = "Escolas", description = "Endpoints para gerenciamento de escolas")
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
    public ResponseEntity<ApiReturn<String>> criarEscola(@RequestBody EscolaRequest request) {
        
        service.salvar(request);
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiReturn.of("Escola criada com sucesso."));
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
    public ResponseEntity<ApiReturn<String>> atualizarEscola(@PathVariable("uuid") UUID uuid,
                                                             @RequestBody EscolaRequest request) {
        service.salvar(uuid, request);
        
        return ResponseEntity.ok(ApiReturn.of("Escola atualizada com sucesso."));
    }

    /**
     * Retorna uma escola pelo UUID.
     * Exemplo de requisição: GET /api/escolas/{uuid}
     */
    @GetMapping("/{uuid}")
    public ResponseEntity<ApiReturn<EscolaView>> buscarEscolaPorUuid(@PathVariable("uuid") UUID uuid) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    /**
     * Lista as escolas ativas com paginação.
     * Exemplo de requisição: GET /api/escolas?page=0&size=10
     */
    @EscolaApiOperation(
            summary = "Lista as escolas",
            description = "Retorna uma página contendo escolas de acordo com os filtros especificados."
    )
    @GetMapping
    public ResponseEntity<ApiReturn<Page<EscolaView>>> listarEscolas(
            @ParameterObject EscolaSpecification specification,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(specification, pageable)));
    }

    @PutMapping("/{uuid}/inativar")
    public ResponseEntity<ApiReturn<String>> inativarEscola(@PathVariable("uuid") UUID uuid) {
        
        service.inativar(uuid);
        
        return ResponseEntity.ok(ApiReturn.of("Escola inativada com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    public ResponseEntity<ApiReturn<String>> ativarEscola(@PathVariable("uuid") UUID uuid) {
        service.ativar(uuid);
        return ResponseEntity.ok(ApiReturn.of("Escola ativada com sucesso."));
    }    

}

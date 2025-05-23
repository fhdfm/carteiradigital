package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.dto.ProdutoRequest;
import com.example.demo.dto.projection.ProdutoView;
import com.example.demo.repository.specification.ProdutoSpecification;
import com.example.demo.service.ProdutoService;
import com.example.demo.util.ApiReturn;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN', 'PDV')")
    @EurekaApiOperation(
            summary = "Criar um produto",
            description = "Cria e persiste um novo produto contendo as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> criarProduto(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um produto",
                    required = true
            )
            @RequestBody @Valid ProdutoRequest request
    ) {
        service.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of("Produto criado com sucesso."));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Atualizar um produto",
            description = "Atualiza, a partir do seu UUID, um produto persistido com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> atualizarProduto(
            @Parameter(description = "UUID do produto a ser atualizado", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um produto",
                    required = true
            )
            @RequestBody @Valid ProdutoRequest request
    ) {
        service.salvar(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Produto atualizado com sucesso."));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Busca um produto",
            description = "Busca, a partir do seu UUID, um produto persistido."
    )
    public ResponseEntity<ApiReturn<ProdutoView>> buscarProdutoPorUuid(
            @Parameter(description = "UUID do produto a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    @GetMapping
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Lista os produtos",
            description = "Retorna um page contendo produtos de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<ProdutoView>>> listarProdutos(
            @ParameterObject ProdutoSpecification specification,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(specification, pageable)));
    }

    @PutMapping("/{uuid}/inativar")
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Inativa um produto",
            description = "Inativa, a partir do seu UUID, um produto persistido."
    )
    public ResponseEntity<ApiReturn<String>> inativarProduto(
            @Parameter(description = "UUID do produto a ser inativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Produto inativado com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Ativa um produto",
            description = "Ativa, a partir do seu UUID, um produto persistido."
    )
    public ResponseEntity<ApiReturn<String>> ativarProduto(
            @Parameter(description = "UUID do produto a ser ativado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Produto ativado com sucesso."));
    }
}

package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.domain.model.CategoriaProduto;
import com.example.demo.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categorias", description = "Endpoints para gerenciamento de categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FUNCIONARIO')")
    @EurekaApiOperation(
            summary = "Criar uma categoria",
            description = "Cria e persiste uma nova categoria contendo as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> criarCategoria(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados da categoria a ser criada",
                    required = true
            )
            @RequestBody @Valid CategoriaProduto categoria
    ) {
        service.salvar(null, categoria.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of("Categoria criado com sucesso."));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Atualizar uma categoria",
            description = "Atualiza, a partir do seu UUID, uma categoria persistida com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> atualizarCategoria(
            @Parameter(description = "UUID da categoria a ser atualizada", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Nome da categoria atualizado",
                    required = true
            )
            @RequestBody @Valid String nome
    ) {
        service.salvar(uuid, nome);
        return ResponseEntity.ok(ApiReturn.of("Categoria atualizado com sucesso."));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Busca uma categoria",
            description = "Busca, a partir do seu UUID, uma categoria persistida."
    )
    public ResponseEntity<ApiReturn<CategoriaProduto>> buscarCategoriaPorUuid(
            @Parameter(description = "UUID da categoria a ser buscada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    @GetMapping
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Lista as categorias",
            description = "Retorna um page contendo categorias de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<CategoriaProduto>>> listarCategorias(
            @Parameter(description = "Nome para filtrar categorias", required = true)
            String nome,

            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(nome, pageable)));
    }

    @PutMapping("/{uuid}/inativar")
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Inativa uma categoria",
            description = "Inativa, a partir do seu UUID, uma categoria persistida."
    )
    public ResponseEntity<ApiReturn<String>> inativarCategoria(
            @Parameter(description = "UUID da categoria a ser inativada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Categoria inativado com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Ativa uma categoria",
            description = "Ativa, a partir do seu UUID, uma categoria persistida."
    )
    public ResponseEntity<ApiReturn<String>> ativarCategoria(
            @Parameter(description = "UUID da categoria a ser ativada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.modificarStatus(uuid);
        return ResponseEntity.ok(ApiReturn.of("Categoria ativado com sucesso."));
    }
}

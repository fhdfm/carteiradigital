package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.dto.pedido.PedidoRequest;
import com.example.demo.dto.projection.pedido.PedidoView;
import com.example.demo.repository.specification.PedidoSpecification;
import com.example.demo.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    /* ────────────────────────  CRIAR  ──────────────────────── */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Criar um pedido",
            description = "Cria e persiste um novo pedido."
    )
    public ResponseEntity<ApiReturn<String>> criarPedido(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de um pedido",
                    required = true
            )
            @RequestBody @Valid PedidoRequest request
    ) {
        service.salvar(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiReturn.of("Pedido criado com sucesso."));
    }

    /* ────────────────────────  ATUALIZAR  ──────────────────────── */

    @PutMapping("/{uuid}")
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Atualizar um pedido",
            description = "Atualiza, a partir do seu UUID, um pedido existente."
    )
    public ResponseEntity<ApiReturn<String>> atualizarPedido(
            @Parameter(description = "UUID do pedido a ser atualizado", required = true)
            @PathVariable("uuid") UUID uuid,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados atualizados do pedido",
                    required = true
            )
            @RequestBody @Valid PedidoRequest request
    ) {
        service.salvar(uuid, request);
        return ResponseEntity.ok(ApiReturn.of("Pedido atualizado com sucesso."));
    }

    /* ────────────────────────  BUSCAR  ──────────────────────── */

    @GetMapping("/{uuid}")
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Busca um pedido",
            description = "Busca, a partir do seu UUID, um pedido persistido."
    )
    public ResponseEntity<ApiReturn<PedidoView>> buscarPedidoPorUuid(
            @Parameter(description = "UUID do pedido a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    /* ────────────────────────  LISTAR  ──────────────────────── */

    @GetMapping
    @PreAuthorize("!hasRole('ALUNO')")
    @EurekaApiOperation(
            summary = "Lista os pedidos",
            description = "Retorna uma página de pedidos conforme filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<PedidoView>>> listarPedidos(
            @ParameterObject PedidoSpecification specification,
            @ParameterObject Pageable pageable
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(specification, pageable)));
    }

    /* ────────────────────────  MODIFICAR STATUS  ──────────────────────── */

    @PutMapping("/{uuid}/cancelar")
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Cancela um pedido",
            description = "Altera o status do pedido para CANCELADO."
    )
    public ResponseEntity<ApiReturn<String>> cancelarPedido(
            @Parameter(description = "UUID do pedido a ser cancelado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.cancelar(uuid);
        return ResponseEntity.ok(ApiReturn.of("Pedido cancelado com sucesso."));
    }

    @PutMapping("/{uuid}/confirmar")
    @PreAuthorize("hasRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Confirma um pedido",
            description = "Altera o status do pedido para CONFIRMADO."
    )
    public ResponseEntity<ApiReturn<String>> confirmarPedido(
            @Parameter(description = "UUID do pedido a ser confirmado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.confirmar(uuid);
        return ResponseEntity.ok(ApiReturn.of("Pedido confirmado com sucesso."));
    }

    @PostMapping("/comprar")
    @PreAuthorize("hasAnyRole('ADMIN', 'PDV')")
    public ResponseEntity<ApiReturn<String>> comprar(@RequestBody @Valid PedidoRequest request) {
        service.comprarAgora(request);
        return ResponseEntity.ok(ApiReturn.of("Compra realizada com sucesso."));
    }

}

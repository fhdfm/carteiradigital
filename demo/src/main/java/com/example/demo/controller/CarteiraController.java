package com.example.demo.controller;

import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.dto.projection.carteira.CarteiraView;
import com.example.demo.service.CarteiraService;
import com.example.demo.service.pagamento.PagamentoService;
import com.example.demo.util.ApiReturn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/aluno/carteira", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Carteira", description = "Endpoints para carteira")
public class CarteiraController {

    private final CarteiraService service;

    public CarteiraController(CarteiraService service) {
        this.service = service;
    }
    @GetMapping
    @PreAuthorize("hasAnyRole('ALUNO', 'RESPONSAVEL')")
    public ResponseEntity<ApiReturn<CarteiraView>> consultarSaldo(
            @Parameter(description = "UUID do aluno dono da carteira a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorAlunoUuid(uuid)));
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN')")
    public ResponseEntity<ApiReturn<String>> realizarRecargaManual(
            @Parameter(description = "UUID do aluno dono da carteira a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid,
            @Parameter(description = "Valor da recarga", required = true)
            @PathVariable("valor") BigDecimal valor
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.realizarRecargaManual(uuid, valor)));
    }

}

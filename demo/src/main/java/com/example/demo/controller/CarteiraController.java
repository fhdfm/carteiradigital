package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.dto.CurrentUserView;
import com.example.demo.dto.RecargaManualRequest;
import com.example.demo.dto.projection.carteira.CarteiraView;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
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
@RequestMapping(path = "/api/aluno/carteira")
@Tag(name = "Carteira", description = "Endpoints para carteira")
public class CarteiraController {

    private final CarteiraService service;

    public CarteiraController(CarteiraService service) {
        this.service = service;
    }
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ALUNO', 'RESPONSAVEL')")
    @EurekaApiOperation(
            summary = "Consulta o saldo da carteira do Aluno",
            description = "Retorna o saldo de o uuid da carteira do aluno."
    )
    public ResponseEntity<ApiReturn<CarteiraView>> consultarSaldo(
            @Parameter(description = "UUID do aluno dono da carteira a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorAlunoUuid(uuid)));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('MASTER', 'ADMIN')")
    @EurekaApiOperation(
            summary = "Realiza uma recarga manual",
            description = "Realiza uma recarga manual para a carteira do aluno."
    )
    public ResponseEntity<ApiReturn<String>> realizarRecargaManual(
            @Parameter(description = "UUID do aluno dono da carteira a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid,

            @RequestBody() RecargaManualRequest request
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.realizarRecargaManual(uuid, request.valor())));
    }

}

package com.example.demo.controller;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.domain.model.Aluno;
import com.example.demo.dto.*;
import com.example.demo.dto.projection.carteira.CarteiraView;
import com.example.demo.service.CarteiraService;
import com.example.demo.util.ApiReturn;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/carteira")
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
    @PreAuthorize("hasAnyRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Realiza uma recarga manual",
            description = "Realiza uma recarga manual para a carteira do aluno."
    )
    public ResponseEntity<ApiReturn<String>> realizarRecargaManual(
            @Parameter(description = "UUID do aluno dono da carteira a ser buscado", required = true)
            @PathVariable("uuid") UUID uuid,

            @RequestBody RecargaManualRequest request
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.realizarRecargaManual(uuid, request.valor())));
    }

    @GetMapping("/buscar-aluno/{numero}")
    @PreAuthorize("hasAnyRole('PDV')")
    @EurekaApiOperation(
            summary = "Consulta o cartão usaurio na carteira",
            description = "Retorna o cartão do aluno."
    )
    public ResponseEntity<ApiReturn<AlunoUsuarioResponse>> consultarCartao(
            @Parameter(description = "Numero do cartão do dono da carteira a ser buscado", required = true)
            @PathVariable("numero") String numero
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarCartaoPorNumero(numero)));
    }

    @PostMapping("/cartao")
    @PreAuthorize("hasAnyRole('ADMIN')")
    @EurekaApiOperation(
            summary = "Cadastra um novo cartão",
            description = "Cadastra um novo cartão a carteira do aluno."
    )
    public ResponseEntity<ApiReturn<String>> cadastrarCartao(
            @RequestBody @Valid CartaoCadastroRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiReturn.of(service.cadastrarCartao(request)));
    }

    @PutMapping("/cartao")
    @PreAuthorize("hasAnyRole('ALUNO', 'RESPONSAVEL')")
    @EurekaApiOperation(
            summary = "Realiza uma recarga manual",
            description = "Realiza uma recarga manual para a carteira do aluno."
    )
    public ResponseEntity<ApiReturn<String>> alterarSenhaCartao(
            @RequestBody @Valid AlteracaoPinRequest request
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.alterarSenhaCartao(request)));
    }

}

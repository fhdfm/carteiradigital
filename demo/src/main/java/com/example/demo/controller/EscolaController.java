package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.demo.dto.EscolaCreationRequest;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.controller.doc.EurekaApiOperation;
import com.example.demo.domain.model.EscolaEndereco;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.model.Escola;
import com.example.demo.domain.model.EscolaFinanceiro;
import com.example.demo.dto.EscolaEnderecoRequest;
import com.example.demo.dto.EscolaFinanceiroRequest;
import com.example.demo.dto.EscolaRequest;
import com.example.demo.dto.UsuarioRequest;
import com.example.demo.dto.projection.escola.EscolaIdAndName;
import com.example.demo.dto.projection.escola.EscolaView;
import com.example.demo.dto.projection.usuario.UsuarioFull;
import com.example.demo.repository.specification.EscolaSpecification;
import com.example.demo.security.SecurityUtils;
import com.example.demo.security.UsuarioLogado;
import com.example.demo.security.accesscontrol.EntityNames;
import com.example.demo.security.accesscontrol.annotation.CheckAccess;
import com.example.demo.service.EscolaEnderecoService;
import com.example.demo.service.EscolaFinanceiroService;
import com.example.demo.service.EscolaResponsavelService;
import com.example.demo.service.EscolaService;
import com.example.demo.util.ApiReturn;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/escolas")
@Tag(name = "Escolas", description = "Endpoints para gerenciamento de escolas")
public class EscolaController {

    private final EscolaService service;
    private final EscolaEnderecoService escolaEnderecoService;
    private final EscolaResponsavelService escolaResponsavelService;
    private final EscolaFinanceiroService escolaFinanceiroService;

    public EscolaController(EscolaService service, EscolaEnderecoService escolaEnderecoService,
        EscolaResponsavelService escolaResponsavelService, EscolaFinanceiroService escolaFinanceiroService) {
        this.service = service;
        this.escolaEnderecoService = escolaEnderecoService;
        this.escolaResponsavelService = escolaResponsavelService;
        this.escolaFinanceiroService = escolaFinanceiroService;
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
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Criar uma escola",
            description = "Cria e persiste uma nova escola contendo as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> criarEscola(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados de uma escola",
                    required = true
            )
            @RequestBody @Valid EscolaCreationRequest request
    ) {
        
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
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Atualizar uma escola",
            description = "Atualiza, a partir do seu UUID, uma escola persistida com as informações especificadas na requisião."
    )
    public ResponseEntity<ApiReturn<String>> atualizarEscola(
            @Parameter(description = "UUID da escola a ser buscada", required = true)
            @PathVariable("uuid") UUID uuid,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Corpo da requisição com os dados da escola",
                    required = true
            )
            @RequestBody @Valid EscolaRequest request
    ) {
        service.salvar(uuid, request);
        
        return ResponseEntity.ok(ApiReturn.of("Escola atualizada com sucesso."));
    }

    /**
     * Retorna uma escola pelo UUID.
     * Exemplo de requisição: GET /api/escolas/{uuid}
     */
    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    @CheckAccess(entity = EntityNames.ESCOLA)
    @EurekaApiOperation(
            summary = "Busca uma escola",
            description = "Busca, a partir do seu UUID, uma escola persistida."
    )
    public ResponseEntity<ApiReturn<EscolaView>> buscarEscolaPorUuid(
            @Parameter(description = "UUID da escola a ser buscada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        return ResponseEntity.ok(ApiReturn.of(service.buscarPorUuid(uuid)));
    }

    @GetMapping("/combobox")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN','FUNCIONARIO')")
    public ResponseEntity<ApiReturn<List<EscolaIdAndName>>> montarCombobox() {

        List<EscolaIdAndName> escolas = new ArrayList<>();

        UsuarioLogado currentUser = SecurityUtils.getUsuarioLogado();
        if (currentUser.possuiPerfil(Perfil.MASTER)) {
            escolas = service.getCombobox();
        } else {
            Escola escola = currentUser.getEscola();
            escolas.add(new EscolaIdAndName() {

                @Override
                public UUID getUuid() {
                    return escola.getUuid();
                }

                @Override
                public String getNome() {
                    return escola.getNome();
                }

            });
        }

        return ResponseEntity.ok(ApiReturn.of(escolas));
    }

    /**
     * Lista as escolas ativas com paginação.
     * Exemplo de requisição: GET /api/escolas?page=0&size=10
     */
    @GetMapping
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Lista as escolas",
            description = "Retorna um page contendo escolas de acordo com os filtros especificados."
    )
    public ResponseEntity<ApiReturn<Page<EscolaView>>> listarEscolas(
            @ParameterObject EscolaSpecification specification,
            @ParameterObject Pageable pageable) {
        return ResponseEntity.ok(ApiReturn.of(service.listar(specification, pageable)));
    }

    @PutMapping("/{uuid}/inativar")
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Inativa uma escola",
            description = "Inativa, a partir do seu UUID, uma escola persistida."
    )
    public ResponseEntity<ApiReturn<String>> inativarEscola(
            @Parameter(description = "UUID da escola a ser inativada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        
        service.inativar(uuid);
        
        return ResponseEntity.ok(ApiReturn.of("Escola inativada com sucesso."));
    }

    @PutMapping("/{uuid}/ativar")
    @PreAuthorize("hasRole('MASTER')")
    @EurekaApiOperation(
            summary = "Ativa uma escola",
            description = "Ativa, a partir do seu UUID, uma escola persistida."
    )
    public ResponseEntity<ApiReturn<String>> ativarEscola(
            @Parameter(description = "UUID da escola a ser ativada", required = true)
            @PathVariable("uuid") UUID uuid
    ) {
        service.ativar(uuid);
        return ResponseEntity.ok(ApiReturn.of("Escola ativada com sucesso."));
    }

//    /**
//     * Atualiza os parametros de uma escola existente identificada pelo UUID.
//     * Exemplo de requisição: PUT /api/escolas/params/{uuid}
//     */
//    @PutMapping("params/{uuid}")
//    @EurekaApiOperation(
//            summary = "Atualizar os parametros de uma escola",
//            description = "Atualiza, a partir do seu UUID, os parametros de uma escola persistida com as informações especificadas na requisião."
//    )
//    public ResponseEntity<ApiReturn<String>> atualizarParametrosEscola(
//            @Parameter(description = "UUID da escola a ser buscada", required = true)
//            @PathVariable("uuid") UUID uuid,
//
//            @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    description = "Corpo da requisição com os parâmetros a serem salvos da escola",
//                    required = true
//            )
//            @RequestBody @Valid EscolaParametrosRequest request
//    ) {
//        service.atualizarParametrosEscola(uuid, request);
//
//        return ResponseEntity.ok(ApiReturn.of("Escola atualizada com sucesso."));
//    }

    @PutMapping("/{uuid}/endereco")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<String>> saveEndereco(@PathVariable("uuid") UUID uuid, @ModelAttribute EscolaEnderecoRequest request) {

        this.escolaEnderecoService.save(uuid, request);

        return ResponseEntity.ok(ApiReturn.of("Atualizado com sucesso."));
    }

    @GetMapping("/{uuid}/endereco")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<EscolaEndereco>> getEndereco(@PathVariable("uuid") UUID uuid) {

        EscolaEndereco endereco = this.escolaEnderecoService.findByEscola(uuid);

        return ResponseEntity.ok(ApiReturn.of(endereco));
    }

    @PutMapping("/{uuid}/responsavel")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<Void>> saveResponsavel(@PathVariable("uuid") UUID uuid, @ModelAttribute UsuarioRequest request) {

        this.escolaResponsavelService.criarOuAtualizarResponsavel(uuid, request);

        return ResponseEntity.ok(ApiReturn.of(null));
    }

    @GetMapping("/{uuid}/responsavel")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<UsuarioFull>> getResponsavel(@PathVariable("uuid") UUID uuid) {

        UsuarioFull responsavel = this.escolaResponsavelService.findResponsavelByEscolaId(uuid);

        return ResponseEntity.ok(ApiReturn.of(responsavel));
    }

    @PutMapping("/{uuid}/financeiro")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<Void>> saveFinanceiro(@PathVariable("uuid") UUID uuid, @ModelAttribute EscolaFinanceiroRequest request) {

        this.escolaFinanceiroService.save(uuid, request);

        return ResponseEntity.ok(ApiReturn.of(null));
    }

    @GetMapping("/{uuid}/financeiro")
    @PreAuthorize("hasAnyRole('MASTER','ADMIN')")
    public ResponseEntity<ApiReturn<EscolaFinanceiro>> getFinanceiro(@PathVariable("uuid") UUID uuid) {

        EscolaFinanceiro financeiro = this.escolaFinanceiroService.findByEscolaId(uuid);

        return ResponseEntity.ok(ApiReturn.of(financeiro));
    }
}

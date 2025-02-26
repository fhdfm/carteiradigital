package com.example.demo.service;

import org.springframework.stereotype.Service;

@Service
public class PagamentoService {
//
//    private final PagamentoRepository repository;
//
//    private final MatriculaService matriculaService;
//
//    private final CursoRepository cursoRepository;
//    private final UsuarioRepository usuarioRepository;
//    private final ApostilaRepository apostilaRepository;
//
//    public PagamentoService(
//            PagamentoRepository repository,
//            MatriculaService matriculaService,
//            UsuarioRepository usuarioRepository,
//            CursoRepository cursoRepository,
//            ApostilaRepository apostilaRepository
//    ) {
//        this.repository = repository;
//        this.matriculaService = matriculaService;
//        this.usuarioRepository = usuarioRepository;
//        this.cursoRepository = cursoRepository;
//        this.apostilaRepository = apostilaRepository;
//    }
//
//    public String registrarPreCompra(ProdutoMercadoPagoRequest produto) {
//        Pagamento pagamento = new Pagamento();
//        pagamento.setTipo(produto.getTipo());
//
//        if (produto.getTipo().equals(TipoProduto.QUESTOES)) {
//            pagamento.setProdutoId(UUID.fromString("00000000-0000-0000-0000-000000000000"));
//            pagamento.setPeriodo(produto.getPeriodo());
//        } else {
//            if (produto.getTipo().equals(TipoProduto.APOSTILA)) {
//
//
//            }
//            pagamento.setProdutoId(produto.getUuid());
//        }
//
//        UsuarioLogado currentUser = SecurityUtil.obterUsuarioLogado();
//
//        pagamento.setUsuarioId(currentUser.getId());
//
//        this.repository.save(pagamento);
//
//        return pagamento.getId().toString();
//    }
//
//    @Transactional
//    public void update(Pagamento pagamento) {
//        Pagamento payment = this.repository.findById(pagamento.getId()).orElse(null);
//        if (payment == null) {
//            payment = new Pagamento();
//            // enviar email pagamento em processamento
//        }
//
//        payment.setStatus(pagamento.getStatus());
//        payment.setData(pagamento.getData());
//        payment.setMpId(pagamento.getMpId());
//        this.repository.save(payment);
//
//        if (payment.isAprovado()) {
//            if (pagamento.getTipo().equals(TipoProduto.APOSTILA)) {
////            ENVIAR EMAIL DE APOSITLA COM ENDERECO
//            } else{
//            // enviar email confirmacao
//                MatriculaRequest matriculaRequest = new MatriculaRequest();
//                matriculaRequest.setProdutoId(payment.getProdutoId());
//                matriculaRequest.setUsuarioId(payment.getUsuarioId());
//                matriculaRequest.setValor(pagamento.getValor());
//                if (payment.getTipo().equals(TipoProduto.QUESTOES))
//                    matriculaRequest.setDataFim(LocalDateTime.now().plusMonths(pagamento.getPeriodo()));
//                this.matriculaService.matricular(matriculaRequest);
//
//            }
//        }
//    }
//
//    public String createPayment(ProdutoMercadoPagoRequest produto) {
//        try {
//            Optional<Usuario> usuario = usuarioRepository.findById(SecurityUtil.obterUsuarioLogado().getId());
//
//            PreferenceClient client = new PreferenceClient();
//
//            // CASO PRECISE ---------------------------------------
////            PreferenceReceiverAddressRequest addressRequest = PreferenceReceiverAddressRequest.builder()
////                    .streetName("")
////                    .cityName("")
////                    .streetNumber("")
////                    .build();
////            PreferenceShipmentsRequest preferenceShipmentsRequest =  PreferenceShipmentsRequest.builder()
////                    .receiverAddress(addressRequest)
////                    .build();
//            // CASO PRECISE ---------------------------------------
//
//            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
//                    .title(produto.getTitulo())
//                    .quantity(1)
//                    .unitPrice(BigDecimal.valueOf(produto.getValor()))
//                    .build();
//
//            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
//                    .email(usuario.get().getEmail())
//                    .build();
//
//            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
//                    .items(Collections.singletonList(itemRequest))
//                    .payer(payerRequest)
//                    .externalReference(registrarPreCompra(produto))
//                    .build();
//
//            Preference preference = client.create(preferenceRequest);
//
//            return preference.getInitPoint();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Erro ao criar pagamento";
//        }
//    }

}

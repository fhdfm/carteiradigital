package com.example.demo.service.pagamento;

import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Pagamento;
import com.example.demo.domain.model.PagamentoItem;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.exception.eureka.EurekaException;
import com.example.demo.repository.PagamentoRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.service.UsuarioService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PagamentoService {
//
    private final PagamentoRepository repository;
    private final UsuarioService usuarioService;
    private final PagamentoProcessorFactory processorFactory;

    public PagamentoService(PagamentoRepository repository, UsuarioService usuarioService, PagamentoProcessorFactory processorFactory) {
        this.repository = repository;
        this.usuarioService = usuarioService;
        this.processorFactory = processorFactory;
    }

    public String registrarPreCompra(List<CriarPagamentoRequest> pagamentoRequestList) {
        Pagamento pagamento = new Pagamento();

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<PagamentoItem> itens = new ArrayList<>();
        for (CriarPagamentoRequest pagamentoRequest : pagamentoRequestList) {
            PagamentoItem item = new PagamentoItem();
            Aluno aluno = usuarioService.findStudentByUuid(pagamentoRequest.alunoUUID());
            item.setAluno(aluno);
            item.setTipo(pagamentoRequest.tipo());
            item.setTitulo(pagamentoRequest.tipo().getDescricao() + " - Aluno: " + item.getAluno().getNome());
            item.setValorIndividual(BigDecimal.valueOf(pagamentoRequest.valor()));
            valorTotal = valorTotal.add(BigDecimal.valueOf(pagamentoRequest.valor()));
            itens.add(item);
        }

        pagamento.setItens(itens);
        pagamento.setData(LocalDateTime.now());
        pagamento.setValorTotal(valorTotal);
        pagamento.setStatus("criando_preference");


        Usuario usuario = usuarioService.findUserByUuid(SecurityUtils.getUsuarioLogado().getUuid());
        pagamento.setUsuarioPagante(usuario);
        repository.save(pagamento);

//        Preference preference = createPayment(pagamento);

        return "OK";
    }

    public String confirmarCompra(UUID pagamentoId) {
        Pagamento pagamento = repository
                .findByUuid(pagamentoId)
                .orElseThrow(() -> EurekaException.ofNotFound("Pagamento não encontrado."));

        pagamento.setStatus("Concluido");
        repository.save(pagamento);

        for (PagamentoItem item : pagamento.getItens()) {
            processorFactory
                    .getProcessor(item.getTipo())
                    .processaPagamento(pagamento.getUsuarioPagante(), item);
        }

        return "OK";
    }
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
//    private Preference createPayment(Pagamento pagamento) {
//        try {
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
//            List<PreferenceItemRequest> itens = pagamento.getItens().stream()
//                    .map(pagamentoItem -> PreferenceItemRequest.builder()
//                            .title(pagamentoItem.getTitulo())
//                            .quantity(1)
//                            .unitPrice(pagamentoItem.getValorIndividual())
//                            .build())
//                    .toList();
//
//            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
//                    .email(pagamento.getUsuarioPagante().getEmail())
//                    .build();
//
//            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
//                    .items(itens)
//                    .payer(payerRequest)
//                    .externalReference(pagamento.getUuid().toString())
//                    .build();
//
//            return client.create(preferenceRequest);
//
//        } catch (Exception e) {
//            throw EurekaException.ofException("Erro ao criar pagamento, tente novamente mais tarde");
//        }
//    }

}

package com.example.demo.service;

import com.example.demo.domain.model.Aluno;
import com.example.demo.domain.model.Pagamento;
import com.example.demo.domain.model.PagamentoItem;
import com.example.demo.domain.model.Usuario;
import com.example.demo.dto.CriarPagamentoRequest;
import com.example.demo.exception.escola.EscolaException;
import com.example.demo.repository.PagamentoRepository;
import com.example.demo.security.SecurityUtils;
import com.example.demo.util.LogUtil;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagamentoService {
//
    private final PagamentoRepository repository;
    private final UsuarioService usuarioService;

    public PagamentoService(PagamentoRepository repository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioService = usuarioService;
    }


    public String registrarPreCompra(List<CriarPagamentoRequest> pagamentoRequestList) {
        Pagamento pagamento = new Pagamento();

        BigDecimal valorTotal = BigDecimal.ZERO;
        List<PagamentoItem> itens = new ArrayList<>();
        for (CriarPagamentoRequest pagamentoRequest : pagamentoRequestList) {
            PagamentoItem item = new PagamentoItem();
            item.setAluno(new Aluno()); // TODO trocar para a consulta de aluno
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


        Usuario usuario = usuarioService.findByUuid(SecurityUtils.getUsuarioLogado().getUuid());
        pagamento.setUsuarioPagante(usuario);
        this.repository.save(pagamento);

        Preference preference = createPayment(pagamento);

        return preference.getInitPoint();
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
    private Preference createPayment(Pagamento pagamento) {
        try {
            MercadoPagoConfig.setAccessToken(pagamento.getItens().getFirst().getAluno().getEscola().getPaymentSecret());
            PreferenceClient client = new PreferenceClient();

            // CASO PRECISE ---------------------------------------
//            PreferenceReceiverAddressRequest addressRequest = PreferenceReceiverAddressRequest.builder()
//                    .streetName("")
//                    .cityName("")
//                    .streetNumber("")
//                    .build();
//            PreferenceShipmentsRequest preferenceShipmentsRequest =  PreferenceShipmentsRequest.builder()
//                    .receiverAddress(addressRequest)
//                    .build();
            // CASO PRECISE ---------------------------------------

            List<PreferenceItemRequest> itens = pagamento.getItens().stream()
                    .map(pagamentoItem -> PreferenceItemRequest.builder()
                            .title(pagamentoItem.getTitulo())
                            .quantity(1)
                            .unitPrice(pagamentoItem.getValorIndividual())
                            .build())
                    .toList();

            PreferencePayerRequest payerRequest = PreferencePayerRequest.builder()
                    .email(pagamento.getUsuarioPagante().getEmail())
                    .build();

            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(itens)
                    .payer(payerRequest)
                    .externalReference(pagamento.getUuid().toString())
                    .build();

            return client.create(preferenceRequest);

        } catch (Exception e) {
            throw EscolaException.ofException("Erro ao criar pagamento, tente novamente mais tarde");
        }
    }

}

package com.example.demo.service.pagamento;

import com.example.demo.domain.enums.TipoPagamento;
import com.example.demo.domain.model.PagamentoItem;
import com.example.demo.domain.model.Usuario;
import com.example.demo.service.CarteiraService;
import com.example.demo.service.pagamento.PagamentoProcessor;
import org.springframework.stereotype.Service;

@Service
public class RecargaCartaoPagamentoProcessor implements PagamentoProcessor {

    private final CarteiraService carteiraService;

    public RecargaCartaoPagamentoProcessor(CarteiraService carteiraService) {
        this.carteiraService = carteiraService;
    }

    @Override
    public void processaPagamento(Usuario usuarioPagante, PagamentoItem pagamentoItem) {
        carteiraService.realizarRecarga(
                pagamentoItem.getAluno().getUuid(),
                pagamentoItem.getValorIndividual(),
                usuarioPagante
        );
    }

    @Override
    public TipoPagamento getTipoSuportado() {
        return TipoPagamento.RECARGA_CARTAO;
    }
}


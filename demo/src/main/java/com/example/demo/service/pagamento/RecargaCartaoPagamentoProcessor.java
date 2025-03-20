package com.example.demo.service.pagamento;

import com.example.demo.domain.enums.TipoPagamento;
import com.example.demo.domain.model.PagamentoItem;
import com.example.demo.service.pagamento.PagamentoProcessor;
import org.springframework.stereotype.Service;

@Service
public class RecargaCartaoPagamentoProcessor implements PagamentoProcessor {
    @Override
    public void processaPagamento(PagamentoItem pagamentoItem) {
        // TODO ADICIONAR Lógica de carga de pagamentos aqui
    }

    @Override
    public TipoPagamento getTipoSuportado() {
        return TipoPagamento.RECARGA_CARTAO;
    }
}


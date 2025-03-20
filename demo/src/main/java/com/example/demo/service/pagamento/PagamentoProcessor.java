package com.example.demo.service.pagamento;

import com.example.demo.domain.enums.TipoPagamento;
import com.example.demo.domain.model.PagamentoItem;

public interface PagamentoProcessor {
    void processaPagamento(PagamentoItem pagamentoItem);
    TipoPagamento getTipoSuportado();
}

package com.example.demo.service.pagamento;

import com.example.demo.domain.enums.TipoPagamento;
import com.example.demo.domain.model.PagamentoItem;
import com.example.demo.domain.model.Usuario;

public interface PagamentoProcessor {
    void processaPagamento(Usuario usuarioPagante, PagamentoItem pagamentoItem);
    TipoPagamento getTipoSuportado();
}

package com.example.demo.service;

import com.example.demo.domain.model.PagamentoItem;
import org.springframework.stereotype.Service;

@Service
public class RecargaCartaoPagamentoProcessor implements PagamentoProcessor {
    @Override
    public void processaPagamento(PagamentoItem pagamentoItem) {
        // TODO ADICIONAR Lógica de carga de pagamentos aqui
    }
}


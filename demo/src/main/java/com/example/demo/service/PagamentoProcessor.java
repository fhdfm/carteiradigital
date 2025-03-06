package com.example.demo.service;

import com.example.demo.domain.model.PagamentoItem;

public interface PagamentoProcessor {
    void processaPagamento(PagamentoItem pagamentoItem);
}

package com.example.demo.dto;

import java.util.UUID;

import com.example.demo.entity.enums.Status;
import com.example.demo.util.CnpjChecker;

public record EscolaView(UUID uuid, String nome, String cnpj, Status status) {

    @Override
    public String cnpj() {
        if (cnpj == null)
            return null;

        CnpjChecker cnpjChecker = new CnpjChecker(cnpj);
        return cnpjChecker.format();
    }


}

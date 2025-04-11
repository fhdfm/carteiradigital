package com.example.demo.dto.projection.carteira;

import java.math.BigDecimal;
import java.util.UUID;

public interface CarteiraView {

    UUID getUuid();

    BigDecimal getSaldo();
}


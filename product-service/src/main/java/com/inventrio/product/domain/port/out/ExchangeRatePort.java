package com.inventrio.product.domain.port.out;

import java.math.BigDecimal;

public interface ExchangeRatePort {
    BigDecimal getExchangeRate(String targetCurrency);
}

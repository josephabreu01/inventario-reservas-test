package com.inventrio.product.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryResponse(
    Long id,
    Long productId,
    BigDecimal price,
    LocalDateTime changedAt
) {}

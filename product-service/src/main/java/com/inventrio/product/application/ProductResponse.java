package com.inventrio.product.application;

import java.math.BigDecimal;

public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    String category,
    String sku
) {}

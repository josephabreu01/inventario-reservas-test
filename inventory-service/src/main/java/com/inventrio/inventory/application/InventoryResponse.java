package com.inventrio.inventory.application;

import java.math.BigDecimal;

public record InventoryResponse(
    Long productId,
    Integer quantity,
    String name,
    BigDecimal price,
    String category
) {}

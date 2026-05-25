package com.inventrio.inventory.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AdjustStockRequest(
    @NotNull(message = "Product ID is required")
    Long productId,

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    Integer quantity,

    @NotBlank(message = "Type is required (ENTRY or EXIT)")
    String type
) {}

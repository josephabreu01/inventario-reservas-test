package com.inventrio.product.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ProductRequest(
    @NotBlank(message = "Name cannot be empty")
    String name,

    @NotBlank(message = "Description cannot be empty")
    String description,

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    BigDecimal price,

    @NotBlank(message = "Category cannot be empty")
    String category,

    @NotBlank(message = "SKU cannot be empty")
    String sku
) {}

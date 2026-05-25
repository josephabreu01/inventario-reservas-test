package com.inventrio.product.infrastructure.adapter.in.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InventoryAdjustedEvent(
    Long productId,
    Integer newQuantity
) {}

package com.inventrio.inventory.infrastructure.adapter.in.event;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductEvent(
    String eventId,
    String eventType,
    Long productId,
    String name,
    String description,
    BigDecimal price,
    String category,
    String sku,
    LocalDateTime timestamp
) {
    public static final String CREATED = "PRODUCT_CREATED";
    public static final String UPDATED = "PRODUCT_UPDATED";
    public static final String DELETED = "PRODUCT_DELETED";
}

package com.inventrio.inventory.application;

import java.time.LocalDateTime;

public record InventoryMovementResponse(
    Long id,
    Long productId,
    Integer quantityChange,
    String movementType,
    LocalDateTime createdAt
) {}

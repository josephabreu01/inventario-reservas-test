package com.inventrio.inventory.infrastructure.adapter.out.event;

import com.inventrio.inventory.domain.model.MovementType;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryAdjustedEvent(
    String eventId,
    String eventType,
    Long productId,
    Integer newQuantity,
    Integer change,
    MovementType type,
    LocalDateTime timestamp
) {
    public static InventoryAdjustedEvent create(Long productId, Integer newQuantity,
                                                Integer change, MovementType type) {
        return new InventoryAdjustedEvent(
            UUID.randomUUID().toString(), "INVENTORY_ADJUSTED",
            productId, newQuantity, change, type, LocalDateTime.now()
        );
    }
}

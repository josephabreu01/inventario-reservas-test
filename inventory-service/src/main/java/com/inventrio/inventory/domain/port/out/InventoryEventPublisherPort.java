package com.inventrio.inventory.domain.port.out;

import com.inventrio.inventory.domain.model.MovementType;

public interface InventoryEventPublisherPort {
    void publishInventoryAdjusted(Long productId, Integer quantity, Integer change, MovementType type);
}

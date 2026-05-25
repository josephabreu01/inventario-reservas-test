package com.inventrio.inventory.domain.port.out;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    Inventory saveInventory(Inventory inventory);
    Optional<Inventory> findInventoryByProductId(Long productId);
    Optional<Inventory> findInventoryByProductIdForUpdate(Long productId);
    List<Inventory> findAllInventories();
    void deleteInventoryByProductId(Long productId);
    void saveMovement(InventoryMovement movement);
    List<InventoryMovement> findMovementsByProductId(Long productId);
    List<InventoryMovement> findMovementsByProductId(Long productId, int page, int size);
    
    // Idempotency support
    boolean isEventProcessed(String eventId);
    void markEventAsProcessed(String eventId);
}

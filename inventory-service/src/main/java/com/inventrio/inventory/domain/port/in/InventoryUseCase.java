package com.inventrio.inventory.domain.port.in;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import java.math.BigDecimal;
import java.util.List;

public interface InventoryUseCase {
    Inventory adjustStock(Long productId, Integer change, MovementType type);
    Inventory getStock(Long productId);
    List<Inventory> getAllStock();
    List<InventoryMovement> getMovements(Long productId);
    List<InventoryMovement> getMovements(Long productId, int page, int size);

    // Operaciones de sincronización con Product Service (via eventos Kafka)
    void initializeStock(Long productId, String name, BigDecimal price, String category);
    void updateProductDetails(Long productId, String name, BigDecimal price, String category);
    void removeProductStock(Long productId);
}

package com.inventrio.inventory.application;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class InventoryApplicationService {

    private final InventoryUseCase useCase;

    public InventoryApplicationService(InventoryUseCase useCase) {
        this.useCase = useCase;
    }

    public InventoryResponse adjustStock(AdjustStockRequest request) {
        MovementType type = MovementType.fromString(request.type());
        Inventory updated = useCase.adjustStock(request.productId(), request.quantity(), type);
        return toResponse(updated);
    }

    public List<InventoryResponse> getAllStock() {
        return useCase.getAllStock().stream().map(this::toResponse).toList();
    }

    public InventoryResponse getStock(Long productId) {
        return toResponse(useCase.getStock(productId));
    }

    public List<InventoryMovementResponse> getMovements(Long productId, int page, int size) {
        return useCase.getMovements(productId, page, size).stream()
                .map(this::toMovementResponse)
                .toList();
    }

    private InventoryResponse toResponse(Inventory inv) {
        return new InventoryResponse(
                inv.getProductId(), inv.getQuantity(),
                inv.getName(), inv.getPrice(), inv.getCategory());
    }

    private InventoryMovementResponse toMovementResponse(InventoryMovement m) {
        return new InventoryMovementResponse(m.getId(), m.getProductId(),
                m.getQuantityChange(), m.getMovementType().name(), m.getCreatedAt());
    }
}

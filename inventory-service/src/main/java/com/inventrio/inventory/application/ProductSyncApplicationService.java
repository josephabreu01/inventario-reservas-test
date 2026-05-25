package com.inventrio.inventory.application;

import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import java.math.BigDecimal;
import java.util.Objects;

@ApplicationScoped
public class ProductSyncApplicationService {

    private final InventoryUseCase inventoryUseCase;

    public ProductSyncApplicationService(InventoryUseCase inventoryUseCase) {
        this.inventoryUseCase = inventoryUseCase;
    }

    public void onProductCreated(Long productId, String name, BigDecimal price, String category) {
        Objects.requireNonNull(productId, "productId cannot be null");
        inventoryUseCase.initializeStock(productId, name, price, category);
    }

    public void onProductUpdated(Long productId, String name, BigDecimal price, String category) {
        Objects.requireNonNull(productId, "productId cannot be null");
        inventoryUseCase.updateProductDetails(productId, name, price, category);
    }

    public void onProductDeleted(Long productId) {
        Objects.requireNonNull(productId, "productId cannot be null");
        inventoryUseCase.removeProductStock(productId);
    }
}

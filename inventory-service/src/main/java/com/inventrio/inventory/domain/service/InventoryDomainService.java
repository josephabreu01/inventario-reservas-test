package com.inventrio.inventory.domain.service;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.exception.NotFoundException;
import com.inventrio.inventory.domain.exception.BadRequestException;
import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import com.inventrio.inventory.domain.port.out.InventoryEventPublisherPort;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InventoryDomainService implements InventoryUseCase {

    private static final Logger LOG = Logger.getLogger(InventoryDomainService.class);

    private final InventoryRepositoryPort repository;
    private final InventoryEventPublisherPort publisher;

    public InventoryDomainService(InventoryRepositoryPort repository, InventoryEventPublisherPort publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public Inventory adjustStock(Long productId, Integer change, MovementType type) {
        if (change <= 0) {
            throw new BadRequestException("Adjustment quantity must be greater than zero");
        }

        Inventory inventory = repository.findInventoryByProductIdForUpdate(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for product: " + productId));

        int newQuantity = calculateNewQuantity(inventory.getQuantity(), change, type);
        inventory.setQuantity(newQuantity);

        Inventory updatedInventory = repository.saveInventory(inventory);
        repository.saveMovement(new InventoryMovement(null, productId, change, type, LocalDateTime.now()));
        publisher.publishInventoryAdjusted(productId, newQuantity, change, type);

        return updatedInventory;
    }

    @Override
    public Inventory getStock(Long productId) {
        return repository.findInventoryByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product inventory not found for ID: " + productId));
    }

    @Override
    public List<Inventory> getAllStock() {
        return repository.findAllInventories();
    }

    @Override
    public List<InventoryMovement> getMovements(Long productId) {
        repository.findInventoryByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product inventory not found for ID: " + productId));
        return repository.findMovementsByProductId(productId);
    }

    @Override
    public List<InventoryMovement> getMovements(Long productId, int page, int size) {
        repository.findInventoryByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product inventory not found for ID: " + productId));
        return repository.findMovementsByProductId(productId, page, size);
    }

    @Override
    public void initializeStock(Long productId, String name, BigDecimal price, String category) {
        if (repository.findInventoryByProductId(productId).isPresent()) {
            LOG.warnf("initializeStock called for productId=%d but inventory already exists — skipping", productId);
            return;
        }
        Inventory inventory = Inventory.builder()
                .productId(productId)
                .quantity(0)
                .name(name)
                .price(price)
                .category(category)
                .build();
        repository.saveInventory(inventory);
    }

    @Override
    public void updateProductDetails(Long productId, String name, BigDecimal price, String category) {
        repository.findInventoryByProductId(productId).ifPresent(inventory -> {
            inventory.setName(name);
            inventory.setPrice(price);
            inventory.setCategory(category);
            repository.saveInventory(inventory);
        });
    }

    @Override
    public void removeProductStock(Long productId) {
        repository.deleteInventoryByProductId(productId);
    }

    private int calculateNewQuantity(int current, int change, MovementType type) {
        return switch (type) {
            case ENTRY -> current + change;
            case EXIT -> {
                validateSufficientStock(current, change);
                yield current - change;
            }
        };
    }

    private void validateSufficientStock(int available, int requested) {
        if (available < requested) {
            throw new BadRequestException(
                    "Insufficient stock. Available: " + available + ", Requested: " + requested);
        }
    }
}

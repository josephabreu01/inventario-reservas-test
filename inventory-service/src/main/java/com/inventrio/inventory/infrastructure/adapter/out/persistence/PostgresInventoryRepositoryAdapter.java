package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidateAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostgresInventoryRepositoryAdapter implements InventoryRepositoryPort {

    private final PanacheInventoryRepository inventoryRepository;
    private final PanacheInventoryMovementRepository movementRepository;
    private final PanacheProcessedEventRepository processedEventRepository;

    public PostgresInventoryRepositoryAdapter(PanacheInventoryRepository inventoryRepository,
                                             PanacheInventoryMovementRepository movementRepository,
                                             PanacheProcessedEventRepository processedEventRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "inventory-stock")
    @CacheInvalidateAll(cacheName = "inventory-all")
    public Inventory saveInventory(Inventory inventory) {
        InventoryEntity entity = InventoryEntity.fromDomain(inventory);
        if (entity.getId() == null) {
            inventoryRepository.persist(entity);
        } else {
            entity = inventoryRepository.getEntityManager().merge(entity);
        }
        return entity.toDomain();
    }

    @Override
    @CacheResult(cacheName = "inventory-stock")
    public Optional<Inventory> findInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId).map(InventoryEntity::toDomain);
    }

    @Override
    @Transactional
    public Optional<Inventory> findInventoryByProductIdForUpdate(Long productId) {
        return inventoryRepository.findByProductIdForUpdate(productId).map(InventoryEntity::toDomain);
    }

    @Override
    @CacheResult(cacheName = "inventory-all")
    public List<Inventory> findAllInventories() {
        return inventoryRepository.listAll().stream()
                .map(InventoryEntity::toDomain)
                .toList();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "inventory-stock")
    @CacheInvalidateAll(cacheName = "inventory-movements")
    @CacheInvalidateAll(cacheName = "inventory-all")
    public void deleteInventoryByProductId(Long productId) {
        // Delete movements first to maintain DB integrity
        movementRepository.delete("productId", productId);
        inventoryRepository.delete("productId", productId);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "inventory-movements")
    public void saveMovement(InventoryMovement movement) {
        InventoryMovementEntity entity = InventoryMovementEntity.fromDomain(movement);
        movementRepository.persist(entity);
    }

    @Override
    @CacheResult(cacheName = "inventory-movements")
    public List<InventoryMovement> findMovementsByProductId(Long productId) {
        return movementRepository.findByProductId(productId).stream()
                .map(InventoryMovementEntity::toDomain)
                .toList();
    }

    @Override
    @CacheResult(cacheName = "inventory-movements")
    public List<InventoryMovement> findMovementsByProductId(Long productId, int page, int size) {
        return movementRepository.findByProductId(productId, page, size).stream()
                .map(InventoryMovementEntity::toDomain)
                .toList();
    }

    @Override
    public boolean isEventProcessed(String eventId) {
        return processedEventRepository.findByIdOptional(eventId).isPresent();
    }

    @Override
    @Transactional
    public void markEventAsProcessed(String eventId) {
        ProcessedEventEntity entity = new ProcessedEventEntity(eventId, LocalDateTime.now());
        processedEventRepository.persist(entity);
    }
}

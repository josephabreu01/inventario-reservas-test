package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import java.util.Optional;

@ApplicationScoped
public class PanacheInventoryRepository implements PanacheRepository<InventoryEntity> {

    public Optional<InventoryEntity> findByProductId(Long productId) {
        return find("productId", productId).firstResultOptional();
    }

    public Optional<InventoryEntity> findByProductIdForUpdate(Long productId) {
        return find("productId", productId)
                .withLock(LockModeType.PESSIMISTIC_WRITE)
                .firstResultOptional();
    }
}

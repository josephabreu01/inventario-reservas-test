package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PanacheInventoryMovementRepository implements PanacheRepository<InventoryMovementEntity> {

    public List<InventoryMovementEntity> findByProductId(Long productId) {
        return list("productId = ?1 order by createdAt desc", productId);
    }

    public List<InventoryMovementEntity> findByProductId(Long productId, int page, int size) {
        return find("productId = ?1 order by createdAt desc", productId)
                .page(page, size)
                .list();
    }
}

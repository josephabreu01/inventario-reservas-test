package com.inventrio.product.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class PanachePriceHistoryRepository implements PanacheRepository<PriceHistoryEntity> {

    public List<PriceHistoryEntity> findByProductId(Long productId) {
        return list("productId = ?1 order by changedAt desc", productId);
    }

    public List<PriceHistoryEntity> findByProductId(Long productId, int page, int size) {
        return find("productId = ?1 order by changedAt desc", productId)
                .page(page, size)
                .list();
    }
}

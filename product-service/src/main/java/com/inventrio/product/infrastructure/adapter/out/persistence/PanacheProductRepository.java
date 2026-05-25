package com.inventrio.product.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PanacheProductRepository implements PanacheRepository<ProductEntity> {

    public Optional<ProductEntity> findBySku(String sku) {
        return find("sku", sku).firstResultOptional();
    }

    public List<ProductEntity> findByCategory(String category) {
        return list("category = ?1 order by id asc", category);
    }

    public List<ProductEntity> findByCategory(String category, int page, int size) {
        return find("category = ?1 order by id asc", category).page(page, size).list();
    }
}

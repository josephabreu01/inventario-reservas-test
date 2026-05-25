package com.inventrio.product.infrastructure.adapter.out.persistence;

import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.out.ProductRepositoryPort;
import io.quarkus.cache.CacheResult;
import io.quarkus.cache.CacheInvalidateAll;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class PostgresProductRepositoryAdapter implements ProductRepositoryPort {

    private final PanacheProductRepository productRepository;
    private final PanachePriceHistoryRepository priceHistoryRepository;

    public PostgresProductRepositoryAdapter(PanacheProductRepository productRepository,
                                            PanachePriceHistoryRepository priceHistoryRepository) {
        this.productRepository = productRepository;
        this.priceHistoryRepository = priceHistoryRepository;
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "products-list")
    @CacheInvalidateAll(cacheName = "product-detail")
    public Product save(Product product) {
        ProductEntity entity = ProductEntity.fromDomain(product);
        if (entity.getId() == null) {
            productRepository.persist(entity);
        } else {
            entity = productRepository.getEntityManager().merge(entity);
        }
        return entity.toDomain();
    }

    @Override
    @CacheResult(cacheName = "product-detail")
    public Optional<Product> findById(Long id) {
        return productRepository.findByIdOptional(id).map(ProductEntity::toDomain);
    }

    @Override
    @CacheResult(cacheName = "products-list")
    public List<Product> findAll() {
        return productRepository.listAll().stream().map(ProductEntity::toDomain).toList();
    }

    @Override
    @CacheResult(cacheName = "products-list")
    public List<Product> findAll(int page, int size) {
        return productRepository.findAll().page(page, size).list().stream().map(ProductEntity::toDomain).toList();
    }

    @Override
    @CacheResult(cacheName = "products-list")
    public List<Product> findByCategory(String category) {
        return productRepository.findByCategory(category).stream().map(ProductEntity::toDomain).toList();
    }

    @Override
    @CacheResult(cacheName = "products-list")
    public List<Product> findByCategory(String category, int page, int size) {
        return productRepository.findByCategory(category, page, size).stream().map(ProductEntity::toDomain).toList();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "products-list")
    @CacheInvalidateAll(cacheName = "product-detail")
    @CacheInvalidateAll(cacheName = "product-stock")
    @CacheInvalidateAll(cacheName = "price-history")
    public boolean delete(Long id) {
        // First delete associated price history
        priceHistoryRepository.delete("productId", id);
        return productRepository.deleteById(id);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return productRepository.findBySku(sku).map(ProductEntity::toDomain);
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "price-history")
    public void savePriceHistory(PriceHistory priceHistory) {
        PriceHistoryEntity entity = PriceHistoryEntity.fromDomain(priceHistory);
        priceHistoryRepository.persist(entity);
    }

    @Override
    @CacheResult(cacheName = "price-history")
    public List<PriceHistory> findPriceHistoryByProductId(Long productId) {
        return priceHistoryRepository.findByProductId(productId).stream().map(PriceHistoryEntity::toDomain).toList();
    }

    @Override
    @CacheResult(cacheName = "price-history")
    public List<PriceHistory> findPriceHistoryByProductId(Long productId, int page, int size) {
        return priceHistoryRepository.findByProductId(productId, page, size).stream().map(PriceHistoryEntity::toDomain).toList();
    }

    @Override
    @Transactional
    @CacheInvalidateAll(cacheName = "product-detail")
    @CacheInvalidateAll(cacheName = "product-stock")
    public void updateCurrentStock(Long productId, Integer newStock) {
        productRepository.findByIdOptional(productId).ifPresent(entity -> {
            entity.setCurrentStock(newStock);
            productRepository.getEntityManager().merge(entity);
        });
    }
}

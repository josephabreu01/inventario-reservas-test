package com.inventrio.product.domain.port.out;

import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import java.util.List;
import java.util.Optional;

public interface ProductRepositoryPort {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findAll(int page, int size);
    List<Product> findByCategory(String category);
    List<Product> findByCategory(String category, int page, int size);
    boolean delete(Long id);
    Optional<Product> findBySku(String sku);
    void savePriceHistory(PriceHistory priceHistory);
    List<PriceHistory> findPriceHistoryByProductId(Long productId);
    List<PriceHistory> findPriceHistoryByProductId(Long productId, int page, int size);
    void updateCurrentStock(Long productId, Integer newStock);
}

package com.inventrio.product.domain.port.in;

import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import java.util.List;

public interface ProductUseCase {
    Product createProduct(Product product);
    Product updateProduct(Long id, Product product);
    void deleteProduct(Long id);
    Product getProduct(Long id, String targetCurrency);
    List<Product> getAllProducts(String targetCurrency);
    List<Product> getAllProducts(int page, int size, String targetCurrency);
    List<Product> getProductsByCategory(String category, String targetCurrency);
    List<Product> getProductsByCategory(String category, int page, int size, String targetCurrency);
    List<PriceHistory> getPriceHistory(Long productId);
    List<PriceHistory> getPriceHistory(Long productId, int page, int size);
    Integer getProductStock(Long productId);
    void syncCurrentStock(Long productId, Integer newStock);
}

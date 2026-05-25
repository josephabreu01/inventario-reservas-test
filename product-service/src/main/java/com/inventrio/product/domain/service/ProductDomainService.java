package com.inventrio.product.domain.service;

import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.exception.NotFoundException;
import com.inventrio.product.domain.exception.ConflictException;
import com.inventrio.product.domain.exception.BadRequestException;
import com.inventrio.product.domain.port.in.ProductUseCase;
import com.inventrio.product.domain.port.out.ExchangeRatePort;
import com.inventrio.product.domain.port.out.ProductEventPublisherPort;
import com.inventrio.product.domain.port.out.ProductRepositoryPort;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class ProductDomainService implements ProductUseCase {

    private final ProductRepositoryPort repository;
    private final ProductEventPublisherPort publisher;
    private final ExchangeRatePort exchangeRatePort;

    public ProductDomainService(ProductRepositoryPort repository,
                                ProductEventPublisherPort publisher,
                                ExchangeRatePort exchangeRatePort) {
        this.repository = repository;
        this.publisher = publisher;
        this.exchangeRatePort = exchangeRatePort;
    }

    @Override
    public Product createProduct(Product product) {
        if (repository.findBySku(product.getSku()).isPresent()) {
            throw new ConflictException("Product with SKU " + product.getSku() + " already exists");
        }

        Product savedProduct = repository.save(product);
        repository.savePriceHistory(new PriceHistory(null, savedProduct.getId(), savedProduct.getPrice(), LocalDateTime.now()));
        publisher.publishProductCreated(savedProduct);

        return savedProduct;
    }

    @Override
    public Product updateProduct(Long id, Product productDetails) {
        Product existingProduct = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));

        boolean skuChanged = !existingProduct.getSku().equals(productDetails.getSku());
        if (skuChanged && repository.findBySku(productDetails.getSku()).isPresent()) {
            throw new ConflictException("Product with SKU " + productDetails.getSku() + " already exists");
        }

        boolean priceChanged = existingProduct.getPrice().compareTo(productDetails.getPrice()) != 0;

        existingProduct.setName(productDetails.getName());
        existingProduct.setDescription(productDetails.getDescription());
        existingProduct.setPrice(productDetails.getPrice());
        existingProduct.setCategory(productDetails.getCategory());
        existingProduct.setSku(productDetails.getSku());

        Product updatedProduct = repository.save(existingProduct);

        if (priceChanged) {
            repository.savePriceHistory(new PriceHistory(null, updatedProduct.getId(), updatedProduct.getPrice(), LocalDateTime.now()));
        }

        publisher.publishProductUpdated(updatedProduct);

        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long id) {
        if (!repository.delete(id)) {
            throw new NotFoundException("Product not found with ID: " + id);
        }
        publisher.publishProductDeleted(id);
    }

    @Override
    public Product getProduct(Long id, String targetCurrency) {
        Product product = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return convertPriceIfNeeded(product, targetCurrency);
    }

    @Override
    public List<Product> getAllProducts(String targetCurrency) {
        return applyRateToList(repository.findAll(), targetCurrency);
    }

    @Override
    public List<Product> getAllProducts(int page, int size, String targetCurrency) {
        return applyRateToList(repository.findAll(page, size), targetCurrency);
    }

    @Override
    public List<Product> getProductsByCategory(String category, String targetCurrency) {
        return applyRateToList(repository.findByCategory(category), targetCurrency);
    }

    @Override
    public List<Product> getProductsByCategory(String category, int page, int size, String targetCurrency) {
        return applyRateToList(repository.findByCategory(category, page, size), targetCurrency);
    }

    @Override
    public List<PriceHistory> getPriceHistory(Long productId) {
        repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
        return repository.findPriceHistoryByProductId(productId);
    }

    @Override
    public List<PriceHistory> getPriceHistory(Long productId, int page, int size) {
        repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
        return repository.findPriceHistoryByProductId(productId, page, size);
    }

    @Override
    public Integer getProductStock(Long productId) {
        return repository.findById(productId)
                .map(Product::getCurrentStock)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + productId));
    }

    @Override
    public void syncCurrentStock(Long productId, Integer newStock) {
        Objects.requireNonNull(productId, "productId cannot be null");
        if (newStock < 0) {
            throw new BadRequestException("Stock cannot be negative: " + newStock);
        }
        repository.findById(productId)
                .ifPresentOrElse(
                    p -> repository.updateCurrentStock(productId, newStock),
                    () -> { throw new NotFoundException("Cannot sync stock: product not found with ID: " + productId); }
                );
    }

    // Fetches rate once and applies it to the entire list — avoids N cache lookups
    private List<Product> applyRateToList(List<Product> products, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.isBlank() || "USD".equalsIgnoreCase(targetCurrency)) {
            return products;
        }
        BigDecimal rate = exchangeRatePort.getExchangeRate(targetCurrency);
        return products.stream().map(p -> applyRate(p, rate)).toList();
    }

    private Product applyRate(Product product, BigDecimal rate) {
        BigDecimal convertedPrice = product.getPrice().multiply(rate).setScale(2, RoundingMode.HALF_UP);
        return Product.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(convertedPrice)
                .category(product.getCategory())
                .sku(product.getSku())
                .currentStock(product.getCurrentStock())
                .build();
    }

    private Product convertPriceIfNeeded(Product product, String targetCurrency) {
        if (targetCurrency == null || targetCurrency.isBlank() || "USD".equalsIgnoreCase(targetCurrency)) {
            return product;
        }
        return applyRate(product, exchangeRatePort.getExchangeRate(targetCurrency));
    }
}

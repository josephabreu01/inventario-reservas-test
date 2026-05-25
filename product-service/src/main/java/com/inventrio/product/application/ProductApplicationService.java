package com.inventrio.product.application;

import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.in.ProductUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProductApplicationService {

    private final ProductUseCase useCase;

    public ProductApplicationService(ProductUseCase useCase) {
        this.useCase = useCase;
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product created = useCase.createProduct(toDomain(request));
        return toResponse(created);
    }

    public List<ProductResponse> getAllProducts(String category, int page, int size, String currency) {
        List<Product> products = (category != null && !category.isBlank())
                ? useCase.getProductsByCategory(category, page, size, currency)
                : useCase.getAllProducts(page, size, currency);
        return products.stream().map(this::toResponse).toList();
    }

    public ProductResponse getProduct(Long id, String currency) {
        return toResponse(useCase.getProduct(id, currency));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        return toResponse(useCase.updateProduct(id, toDomain(request)));
    }

    public void deleteProduct(Long id) {
        useCase.deleteProduct(id);
    }

    public StockResponse getProductStock(Long id) {
        Integer quantity = useCase.getProductStock(id);
        return new StockResponse(id, quantity);
    }

    public List<PriceHistoryResponse> getPriceHistory(Long id, int page, int size) {
        return useCase.getPriceHistory(id, page, size).stream()
                .map(this::toPriceHistoryResponse)
                .toList();
    }

    private Product toDomain(ProductRequest r) {
        return Product.builder()
                .name(r.name())
                .description(r.description())
                .price(r.price())
                .category(r.category())
                .sku(r.sku())
                .build();
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(),
                p.getPrice(), p.getCategory(), p.getSku());
    }

    private PriceHistoryResponse toPriceHistoryResponse(PriceHistory ph) {
        return new PriceHistoryResponse(ph.getId(), ph.getProductId(), ph.getPrice(), ph.getChangedAt());
    }
}

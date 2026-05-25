package com.inventrio.product.infrastructure.adapter.out.event;

import com.inventrio.product.domain.model.ProductEventType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductEvent(
    String eventId,
    ProductEventType eventType,
    Long productId,
    String name,
    String description,
    BigDecimal price,
    String category,
    String sku,
    LocalDateTime timestamp
) {
    public static ProductEvent created(Long productId, String name, String description,
                                       BigDecimal price, String category, String sku) {
        return new ProductEvent(UUID.randomUUID().toString(), ProductEventType.PRODUCT_CREATED,
                productId, name, description, price, category, sku, LocalDateTime.now());
    }

    public static ProductEvent updated(Long productId, String name, String description,
                                       BigDecimal price, String category, String sku) {
        return new ProductEvent(UUID.randomUUID().toString(), ProductEventType.PRODUCT_UPDATED,
                productId, name, description, price, category, sku, LocalDateTime.now());
    }

    public static ProductEvent deleted(Long productId) {
        return new ProductEvent(UUID.randomUUID().toString(), ProductEventType.PRODUCT_DELETED,
                productId, null, null, null, null, null, LocalDateTime.now());
    }
}

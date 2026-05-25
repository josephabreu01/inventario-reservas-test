package com.inventrio.product.domain.port.out;

import com.inventrio.product.domain.model.Product;

public interface ProductEventPublisherPort {
    void publishProductCreated(Product product);
    void publishProductUpdated(Product product);
    void publishProductDeleted(Long id);
}

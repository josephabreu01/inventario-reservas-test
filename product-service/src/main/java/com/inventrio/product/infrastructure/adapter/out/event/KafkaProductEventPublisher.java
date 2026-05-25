package com.inventrio.product.infrastructure.adapter.out.event;

import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.out.ProductEventPublisherPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaProductEventPublisher implements ProductEventPublisherPort {

    private final Emitter<ProductEvent> emitter;

    public KafkaProductEventPublisher(@Channel("product-events-out") Emitter<ProductEvent> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publishProductCreated(Product product) {
        emitter.send(ProductEvent.created(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getSku()
        ));
    }

    @Override
    public void publishProductUpdated(Product product) {
        emitter.send(ProductEvent.updated(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getSku()
        ));
    }

    @Override
    public void publishProductDeleted(Long id) {
        emitter.send(ProductEvent.deleted(id));
    }
}

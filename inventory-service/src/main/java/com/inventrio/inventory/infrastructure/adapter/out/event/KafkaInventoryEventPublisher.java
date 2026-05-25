package com.inventrio.inventory.infrastructure.adapter.out.event;

import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.port.out.InventoryEventPublisherPort;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ApplicationScoped
public class KafkaInventoryEventPublisher implements InventoryEventPublisherPort {

    private final Emitter<InventoryAdjustedEvent> emitter;

    public KafkaInventoryEventPublisher(@Channel("inventory-events-out") Emitter<InventoryAdjustedEvent> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void publishInventoryAdjusted(Long productId, Integer quantity, Integer change, MovementType type) {
        emitter.send(InventoryAdjustedEvent.create(productId, quantity, change, type));
    }
}

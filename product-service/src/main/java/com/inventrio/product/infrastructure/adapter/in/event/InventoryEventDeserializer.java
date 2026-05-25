package com.inventrio.product.infrastructure.adapter.in.event;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class InventoryEventDeserializer extends ObjectMapperDeserializer<InventoryAdjustedEvent> {
    public InventoryEventDeserializer() {
        super(InventoryAdjustedEvent.class);
    }
}

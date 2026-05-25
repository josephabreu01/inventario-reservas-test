package com.inventrio.inventory.infrastructure.adapter.in.event;

import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class ProductEventDeserializer extends ObjectMapperDeserializer<ProductEvent> {
    public ProductEventDeserializer() {
        super(ProductEvent.class);
    }
}

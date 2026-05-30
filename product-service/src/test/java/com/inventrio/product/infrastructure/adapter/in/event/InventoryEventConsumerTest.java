package com.inventrio.product.infrastructure.adapter.in.event;

import static org.mockito.Mockito.*;

import com.inventrio.product.domain.port.in.ProductUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryEventConsumerTest {

    private ProductUseCase productUseCase;
    private InventoryEventConsumer consumer;

    @BeforeEach
    void setUp() {
        productUseCase = mock(ProductUseCase.class);
        consumer = new InventoryEventConsumer(productUseCase);
    }

    @Test
    void consume_validEvent_syncsCalled() {
        InventoryAdjustedEvent event = new InventoryAdjustedEvent(100L, 50);

        consumer.consume(event);

        verify(productUseCase).syncCurrentStock(100L, 50);
    }

    @Test
    void consume_nullProductId_skipsProcessing() {
        InventoryAdjustedEvent event = new InventoryAdjustedEvent(null, 50);

        consumer.consume(event);

        verify(productUseCase, never()).syncCurrentStock(any(), any());
    }

    @Test
    void consume_nullNewQuantity_skipsProcessing() {
        InventoryAdjustedEvent event = new InventoryAdjustedEvent(100L, null);

        consumer.consume(event);

        verify(productUseCase, never()).syncCurrentStock(any(), any());
    }

    @Test
    void consume_bothFieldsNull_skipsProcessing() {
        InventoryAdjustedEvent event = new InventoryAdjustedEvent(null, null);

        consumer.consume(event);

        verify(productUseCase, never()).syncCurrentStock(any(), any());
    }
}

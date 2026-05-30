package com.inventrio.inventory.infrastructure.adapter.in.event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.inventory.application.ProductSyncApplicationService;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductEventConsumerTest {

    private ProductSyncApplicationService productSyncService;
    private InventoryRepositoryPort repositoryPort;
    private ProductEventConsumer consumer;

    @BeforeEach
    void setUp() {
        productSyncService = mock(ProductSyncApplicationService.class);
        repositoryPort = mock(InventoryRepositoryPort.class);
        consumer = new ProductEventConsumer(productSyncService, repositoryPort);
    }

    private ProductEvent createdEvent(String eventId) {
        return new ProductEvent(eventId, ProductEvent.CREATED, 100L,
                "Laptop", "Gaming Laptop", new BigDecimal("1200.00"), "Tech", "SKU-1", LocalDateTime.now());
    }

    @Test
    void consume_createdEvent_routesToOnProductCreated() {
        ProductEvent event = createdEvent("evt-001");
        when(repositoryPort.isEventProcessed("evt-001")).thenReturn(false);

        consumer.consume(event);

        verify(productSyncService).onProductCreated(100L, "Laptop", new BigDecimal("1200.00"), "Tech");
        verify(repositoryPort).markEventAsProcessed("evt-001");
    }

    @Test
    void consume_updatedEvent_routesToOnProductUpdated() {
        ProductEvent event = new ProductEvent("evt-002", ProductEvent.UPDATED, 100L,
                "Laptop Pro", "Gaming Laptop V2", new BigDecimal("1500.00"), "Tech", "SKU-1", LocalDateTime.now());
        when(repositoryPort.isEventProcessed("evt-002")).thenReturn(false);

        consumer.consume(event);

        verify(productSyncService).onProductUpdated(100L, "Laptop Pro", new BigDecimal("1500.00"), "Tech");
        verify(repositoryPort).markEventAsProcessed("evt-002");
    }

    @Test
    void consume_deletedEvent_routesToOnProductDeleted() {
        ProductEvent event = new ProductEvent("evt-003", ProductEvent.DELETED, 100L,
                null, null, null, null, null, LocalDateTime.now());
        when(repositoryPort.isEventProcessed("evt-003")).thenReturn(false);

        consumer.consume(event);

        verify(productSyncService).onProductDeleted(100L);
        verify(repositoryPort).markEventAsProcessed("evt-003");
    }

    @Test
    void consume_alreadyProcessed_skipsRouting() {
        ProductEvent event = createdEvent("evt-004");
        when(repositoryPort.isEventProcessed("evt-004")).thenReturn(true);

        consumer.consume(event);

        verify(productSyncService, never()).onProductCreated(any(), any(), any(), any());
        verify(repositoryPort, never()).markEventAsProcessed(any());
    }

    @Test
    void consume_routingThrowsException_doesNotMarkProcessed() {
        ProductEvent event = createdEvent("evt-005");
        when(repositoryPort.isEventProcessed("evt-005")).thenReturn(false);
        doThrow(new RuntimeException("DB error"))
                .when(productSyncService).onProductCreated(any(), any(), any(), any());

        assertThrows(RuntimeException.class, () -> consumer.consume(event));

        verify(repositoryPort, never()).markEventAsProcessed("evt-005");
    }

    @Test
    void consume_unknownEventType_skipsAndMarksProcessed() {
        ProductEvent event = new ProductEvent("evt-006", "UNKNOWN_TYPE", 100L,
                "Laptop", null, null, "Tech", null, LocalDateTime.now());
        when(repositoryPort.isEventProcessed("evt-006")).thenReturn(false);

        consumer.consume(event);

        verify(productSyncService, never()).onProductCreated(any(), any(), any(), any());
        verify(productSyncService, never()).onProductUpdated(any(), any(), any(), any());
        verify(productSyncService, never()).onProductDeleted(any());
        verify(repositoryPort).markEventAsProcessed("evt-006");
    }
}

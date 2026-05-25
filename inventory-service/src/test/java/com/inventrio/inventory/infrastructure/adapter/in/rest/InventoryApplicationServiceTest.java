package com.inventrio.inventory.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.inventory.application.AdjustStockRequest;
import com.inventrio.inventory.application.InventoryApplicationService;
import com.inventrio.inventory.application.InventoryMovementResponse;
import com.inventrio.inventory.application.InventoryResponse;
import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryApplicationServiceTest {

    private InventoryUseCase useCase;
    private InventoryApplicationService service;

    @BeforeEach
    void setUp() {
        useCase = mock(InventoryUseCase.class);
        service = new InventoryApplicationService(useCase);
    }

    @Test
    void adjustStock_mapsRequestAndReturnsResponse() {
        AdjustStockRequest request = new AdjustStockRequest(1L, 10, "ENTRY");
        Inventory updated = Inventory.builder()
                .id(1L).productId(1L).quantity(60)
                .name("Laptop").price(new BigDecimal("1200.00")).category("Tech")
                .build();
        when(useCase.adjustStock(1L, 10, MovementType.ENTRY)).thenReturn(updated);

        InventoryResponse response = service.adjustStock(request);

        assertNotNull(response);
        assertEquals(1L, response.productId());
        assertEquals(60, response.quantity());
        assertEquals("Laptop", response.name());
        assertEquals("Tech", response.category());
        verify(useCase).adjustStock(1L, 10, MovementType.ENTRY);
    }

    @Test
    void getAllStock_returnsMappedList() {
        List<Inventory> inventories = List.of(
                Inventory.builder().id(1L).productId(1L).quantity(100).name("Mouse").category("Periféricos").build(),
                Inventory.builder().id(2L).productId(2L).quantity(50).name("Teclado").category("Periféricos").build()
        );
        when(useCase.getAllStock()).thenReturn(inventories);

        List<InventoryResponse> result = service.getAllStock();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).productId());
        assertEquals(100, result.get(0).quantity());
        assertEquals("Mouse", result.get(0).name());
        assertEquals(2L, result.get(1).productId());
    }

    @Test
    void getStock_returnsResponse() {
        Inventory inv = Inventory.builder()
                .id(1L).productId(5L).quantity(30)
                .name("Monitor").price(new BigDecimal("500.00")).category("Tech")
                .build();
        when(useCase.getStock(5L)).thenReturn(inv);

        InventoryResponse response = service.getStock(5L);

        assertEquals(5L, response.productId());
        assertEquals(30, response.quantity());
        assertEquals("Monitor", response.name());
    }

    @Test
    void getMovements_returnsMappedList() {
        InventoryMovement m = new InventoryMovement(1L, 5L, 10, MovementType.ENTRY, LocalDateTime.now());
        when(useCase.getMovements(5L, 0, 20)).thenReturn(List.of(m));

        List<InventoryMovementResponse> result = service.getMovements(5L, 0, 20);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).productId());
        assertEquals(10, result.get(0).quantityChange());
        assertEquals("ENTRY", result.get(0).movementType());
    }
}

package com.inventrio.inventory.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.inventory.domain.port.in.InventoryUseCase;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductSyncApplicationServiceTest {

    private InventoryUseCase inventoryUseCase;
    private ProductSyncApplicationService service;

    @BeforeEach
    void setUp() {
        inventoryUseCase = mock(InventoryUseCase.class);
        service = new ProductSyncApplicationService(inventoryUseCase);
    }

    @Test
    void onProductCreated_delegatesToUseCase() {
        service.onProductCreated(100L, "Laptop", new BigDecimal("1200.00"), "Tech");

        verify(inventoryUseCase).initializeStock(100L, "Laptop", new BigDecimal("1200.00"), "Tech");
    }

    @Test
    void onProductCreated_nullProductId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> service.onProductCreated(null, "Laptop", new BigDecimal("1200.00"), "Tech"));

        verify(inventoryUseCase, never()).initializeStock(any(), any(), any(), any());
    }

    @Test
    void onProductUpdated_delegatesToUseCase() {
        service.onProductUpdated(100L, "Laptop Pro", new BigDecimal("1500.00"), "Tech");

        verify(inventoryUseCase).updateProductDetails(100L, "Laptop Pro", new BigDecimal("1500.00"), "Tech");
    }

    @Test
    void onProductUpdated_nullProductId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> service.onProductUpdated(null, "Laptop Pro", new BigDecimal("1500.00"), "Tech"));

        verify(inventoryUseCase, never()).updateProductDetails(any(), any(), any(), any());
    }

    @Test
    void onProductDeleted_delegatesToUseCase() {
        service.onProductDeleted(100L);

        verify(inventoryUseCase).removeProductStock(100L);
    }

    @Test
    void onProductDeleted_nullProductId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> service.onProductDeleted(null));

        verify(inventoryUseCase, never()).removeProductStock(any());
    }
}

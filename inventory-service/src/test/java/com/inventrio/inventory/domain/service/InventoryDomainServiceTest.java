package com.inventrio.inventory.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.inventory.domain.exception.BadRequestException;
import com.inventrio.inventory.domain.exception.NotFoundException;
import com.inventrio.inventory.domain.model.Inventory;
import com.inventrio.inventory.domain.model.InventoryMovement;
import com.inventrio.inventory.domain.model.MovementType;
import com.inventrio.inventory.domain.port.out.InventoryEventPublisherPort;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InventoryDomainServiceTest {

    private InventoryRepositoryPort repository;
    private InventoryEventPublisherPort publisher;
    private InventoryDomainService domainService;

    @BeforeEach
    void setUp() {
        repository = mock(InventoryRepositoryPort.class);
        publisher = mock(InventoryEventPublisherPort.class);
        domainService = new InventoryDomainService(repository, publisher);
    }

    @Test
    void testAdjustStock_Entry_Success() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(50).build();
        Inventory updated = Inventory.builder().id(1L).productId(100L).quantity(70).build();

        when(repository.findInventoryByProductIdForUpdate(100L)).thenReturn(Optional.of(existing));
        when(repository.saveInventory(any(Inventory.class))).thenReturn(updated);

        Inventory result = domainService.adjustStock(100L, 20, MovementType.ENTRY);

        assertEquals(70, result.getQuantity());
        verify(repository).saveInventory(existing);
        verify(repository).saveMovement(any(InventoryMovement.class));
        verify(publisher).publishInventoryAdjusted(100L, 70, 20, MovementType.ENTRY);
    }

    @Test
    void testAdjustStock_Exit_Success() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(50).build();
        Inventory updated = Inventory.builder().id(1L).productId(100L).quantity(30).build();

        when(repository.findInventoryByProductIdForUpdate(100L)).thenReturn(Optional.of(existing));
        when(repository.saveInventory(any(Inventory.class))).thenReturn(updated);

        Inventory result = domainService.adjustStock(100L, 20, MovementType.EXIT);

        assertEquals(30, result.getQuantity());
        verify(repository).saveInventory(existing);
        verify(repository).saveMovement(any(InventoryMovement.class));
        verify(publisher).publishInventoryAdjusted(100L, 30, 20, MovementType.EXIT);
    }

    @Test
    void testAdjustStock_Exit_InsufficientStock() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(10).build();

        when(repository.findInventoryByProductIdForUpdate(100L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> domainService.adjustStock(100L, 20, MovementType.EXIT));

        verify(repository, never()).saveInventory(any());
        verify(publisher, never()).publishInventoryAdjusted(any(), any(), any(), any());
    }

    @Test
    void testInitializeStock_Success() {
        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.empty());

        domainService.initializeStock(100L, "Laptop", new BigDecimal("1200.00"), "Tech");

        verify(repository).saveInventory(argThat(inv ->
                inv.getProductId().equals(100L) &&
                inv.getQuantity() == 0 &&
                "Laptop".equals(inv.getName()) &&
                "Tech".equals(inv.getCategory())
        ));
    }

    @Test
    void testInitializeStock_AlreadyExists_DoesNothing() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(5).build();
        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.of(existing));

        domainService.initializeStock(100L, "Laptop", new BigDecimal("1200.00"), "Tech");

        verify(repository, never()).saveInventory(any());
    }

    @Test
    void testUpdateProductDetails_UpdatesFields() {
        Inventory existing = Inventory.builder()
                .id(1L).productId(100L).quantity(50)
                .name("Old Name").price(new BigDecimal("100.00")).category("OldCat")
                .build();
        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.of(existing));

        domainService.updateProductDetails(100L, "New Name", new BigDecimal("150.00"), "NewCat");

        verify(repository).saveInventory(argThat(inv ->
                "New Name".equals(inv.getName()) &&
                new BigDecimal("150.00").equals(inv.getPrice()) &&
                "NewCat".equals(inv.getCategory()) &&
                inv.getQuantity() == 50
        ));
    }

    @Test
    void testUpdateProductDetails_NotFound_DoesNothing() {
        when(repository.findInventoryByProductId(999L)).thenReturn(Optional.empty());

        domainService.updateProductDetails(999L, "Name", new BigDecimal("50.00"), "Cat");

        verify(repository, never()).saveInventory(any());
    }

    @Test
    void testRemoveProductStock_DeletesInventory() {
        domainService.removeProductStock(100L);

        verify(repository).deleteInventoryByProductId(100L);
    }

    @Test
    void testAdjustStock_InvalidType_ThrowsBadRequestException() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(50).build();
        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> domainService.adjustStock(100L, 10, MovementType.fromString("INVALID")));
        verify(repository, never()).saveInventory(any());
    }

    @Test
    void testGetStock_NotFound_ThrowsNotFoundException() {
        when(repository.findInventoryByProductId(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> domainService.getStock(99L));
    }

    @Test
    void testGetAllStock_ReturnsList() {
        List<Inventory> list = List.of(
                Inventory.builder().id(1L).productId(1L).quantity(100).build(),
                Inventory.builder().id(2L).productId(2L).quantity(50).build()
        );
        when(repository.findAllInventories()).thenReturn(list);

        List<Inventory> result = domainService.getAllStock();

        assertEquals(2, result.size());
        verify(repository).findAllInventories();
    }

    @Test
    void testAdjustStock_ZeroChange_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> domainService.adjustStock(100L, 0, MovementType.ENTRY));
        verify(repository, never()).saveInventory(any());
    }

    @Test
    void testAdjustStock_NegativeChange_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class,
                () -> domainService.adjustStock(100L, -5, MovementType.ENTRY));
        verify(repository, never()).saveInventory(any());
    }

    @Test
    void testGetStock_Found_ReturnsInventory() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(50).build();
        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.of(existing));

        Inventory result = domainService.getStock(100L);

        assertEquals(50, result.getQuantity());
        assertEquals(100L, result.getProductId());
    }

    @Test
    void testGetMovements_PaginatedVariant_ReturnsList() {
        Inventory existing = Inventory.builder().id(1L).productId(100L).quantity(50).build();
        InventoryMovement m1 = InventoryMovement.builder().id(1L).productId(100L).quantityChange(10)
                .movementType(MovementType.ENTRY).build();
        InventoryMovement m2 = InventoryMovement.builder().id(2L).productId(100L).quantityChange(5)
                .movementType(MovementType.EXIT).build();

        when(repository.findInventoryByProductId(100L)).thenReturn(Optional.of(existing));
        when(repository.findMovementsByProductId(100L, 0, 10)).thenReturn(List.of(m1, m2));

        List<InventoryMovement> result = domainService.getMovements(100L, 0, 10);

        assertEquals(2, result.size());
        verify(repository).findMovementsByProductId(100L, 0, 10);
    }

    @Test
    void testGetMovements_PaginatedVariant_NotFound_ThrowsNotFoundException() {
        when(repository.findInventoryByProductId(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> domainService.getMovements(999L, 0, 10));
        verify(repository, never()).findMovementsByProductId(any(), anyInt(), anyInt());
    }
}

package com.inventrio.product.infrastructure.adapter.in.rest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.product.application.PriceHistoryResponse;
import com.inventrio.product.application.ProductApplicationService;
import com.inventrio.product.application.ProductRequest;
import com.inventrio.product.application.ProductResponse;
import com.inventrio.product.application.StockResponse;
import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.in.ProductUseCase;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductApplicationServiceTest {

    private ProductUseCase useCase;
    private ProductApplicationService service;

    @BeforeEach
    void setUp() {
        useCase = mock(ProductUseCase.class);
        service = new ProductApplicationService(useCase);
    }

    @Test
    void createProduct_mapsRequestAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Laptop", "Gaming", new BigDecimal("1200.00"), "Tech", "SKU1");
        Product saved = Product.builder().id(1L).name("Laptop").description("Gaming")
                .price(new BigDecimal("1200.00")).category("Tech").sku("SKU1").build();
        when(useCase.createProduct(any(Product.class))).thenReturn(saved);

        ProductResponse response = service.createProduct(request);

        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Laptop", response.name());
        assertEquals("SKU1", response.sku());
        verify(useCase).createProduct(any(Product.class));
    }

    @Test
    void getAllProducts_withCategory_callsGetByCategory() {
        Product p = Product.builder().id(1L).name("Mouse").description("USB Mouse")
                .price(new BigDecimal("25.00")).category("Peripherals").sku("SKU2").build();
        when(useCase.getProductsByCategory(eq("Peripherals"), eq(0), eq(20), isNull())).thenReturn(List.of(p));

        List<ProductResponse> result = service.getAllProducts("Peripherals", 0, 20, null);

        assertEquals(1, result.size());
        verify(useCase).getProductsByCategory("Peripherals", 0, 20, null);
        verify(useCase, never()).getAllProducts(anyInt(), anyInt(), any());
    }

    @Test
    void getAllProducts_withoutCategory_callsGetAll() {
        when(useCase.getAllProducts(0, 20, null)).thenReturn(List.of());

        List<ProductResponse> result = service.getAllProducts(null, 0, 20, null);

        assertEquals(0, result.size());
        verify(useCase).getAllProducts(0, 20, null);
        verify(useCase, never()).getProductsByCategory(any(), anyInt(), anyInt(), any());
    }

    @Test
    void getAllProducts_blankCategory_callsGetAll() {
        when(useCase.getAllProducts(0, 20, null)).thenReturn(List.of());

        service.getAllProducts("  ", 0, 20, null);

        verify(useCase).getAllProducts(0, 20, null);
    }

    @Test
    void getProduct_returnsResponse() {
        Product p = Product.builder().id(2L).name("Monitor").description("4K")
                .price(new BigDecimal("500.00")).category("Tech").sku("SKU3").build();
        when(useCase.getProduct(2L, "EUR")).thenReturn(p);

        ProductResponse response = service.getProduct(2L, "EUR");

        assertEquals(2L, response.id());
        assertEquals("Monitor", response.name());
    }

    @Test
    void updateProduct_mapsRequestAndReturnsResponse() {
        ProductRequest request = new ProductRequest("Monitor Pro", "4K HDR", new BigDecimal("600.00"), "Tech", "SKU3");
        Product updated = Product.builder().id(2L).name("Monitor Pro").description("4K HDR")
                .price(new BigDecimal("600.00")).category("Tech").sku("SKU3").build();
        when(useCase.updateProduct(eq(2L), any(Product.class))).thenReturn(updated);

        ProductResponse response = service.updateProduct(2L, request);

        assertEquals("Monitor Pro", response.name());
        assertEquals(new BigDecimal("600.00"), response.price());
    }

    @Test
    void deleteProduct_delegatesToUseCase() {
        service.deleteProduct(5L);
        verify(useCase).deleteProduct(5L);
    }

    @Test
    void getProductStock_returnsStockResponse() {
        when(useCase.getProductStock(3L)).thenReturn(42);

        StockResponse response = service.getProductStock(3L);

        assertEquals(3L, response.productId());
        assertEquals(42, response.quantity());
    }

    @Test
    void getPriceHistory_returnsMappedList() {
        PriceHistory ph = new PriceHistory(1L, 3L, new BigDecimal("100.00"), LocalDateTime.now());
        when(useCase.getPriceHistory(3L, 0, 10)).thenReturn(List.of(ph));

        List<PriceHistoryResponse> result = service.getPriceHistory(3L, 0, 10);

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("100.00"), result.get(0).price());
    }
}

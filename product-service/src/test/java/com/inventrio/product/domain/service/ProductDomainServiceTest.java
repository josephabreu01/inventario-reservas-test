package com.inventrio.product.domain.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.inventrio.product.domain.exception.BadRequestException;
import com.inventrio.product.domain.exception.ConflictException;
import com.inventrio.product.domain.exception.NotFoundException;
import com.inventrio.product.domain.model.PriceHistory;
import com.inventrio.product.domain.model.Product;
import com.inventrio.product.domain.port.out.ExchangeRatePort;
import com.inventrio.product.domain.port.out.ProductEventPublisherPort;
import com.inventrio.product.domain.port.out.ProductRepositoryPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductDomainServiceTest {

    private ProductRepositoryPort repository;
    private ProductEventPublisherPort publisher;
    private ExchangeRatePort exchangeRatePort;
    private ProductDomainService domainService;

    @BeforeEach
    void setUp() {
        repository = mock(ProductRepositoryPort.class);
        publisher = mock(ProductEventPublisherPort.class);
        exchangeRatePort = mock(ExchangeRatePort.class);
        domainService = new ProductDomainService(repository, publisher, exchangeRatePort);
    }

    @Test
    void testCreateProduct_Success() {
        Product newProduct = Product.builder().name("Laptop").description("Gaming Laptop")
                .price(new BigDecimal("1200.00")).category("Tech").sku("SKU123").build();
        Product savedProduct = Product.builder().id(1L).name("Laptop").description("Gaming Laptop")
                .price(new BigDecimal("1200.00")).category("Tech").sku("SKU123").build();

        when(repository.findBySku("SKU123")).thenReturn(Optional.empty());
        when(repository.save(any(Product.class))).thenReturn(savedProduct);

        Product result = domainService.createProduct(newProduct);

        assertNotNull(result.getId());
        assertEquals("Laptop", result.getName());
        verify(repository).save(newProduct);
        verify(repository).savePriceHistory(any(PriceHistory.class));
        verify(publisher).publishProductCreated(savedProduct);
    }

    @Test
    void testCreateProduct_SkuConflict() {
        Product product = Product.builder().name("Laptop").description("Gaming Laptop")
                .price(new BigDecimal("1200.00")).category("Tech").sku("SKU123").build();
        when(repository.findBySku("SKU123")).thenReturn(Optional.of(product));

        assertThrows(ConflictException.class, () -> domainService.createProduct(product));

        verify(repository, never()).save(any());
        verify(publisher, never()).publishProductCreated(any());
    }

    @Test
    void testGetProduct_WithCurrencyConversion() {
        Product product = Product.builder().id(1L).name("Laptop").description("Gaming Laptop")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU123").currentStock(5).build();
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(exchangeRatePort.getExchangeRate("EUR")).thenReturn(new BigDecimal("0.90"));

        Product result = domainService.getProduct(1L, "EUR");

        assertEquals(new BigDecimal("90.00"), result.getPrice());
        assertEquals(5, result.getCurrentStock()); // currentStock must be preserved after conversion
        verify(exchangeRatePort).getExchangeRate("EUR");
    }

    @Test
    void testUpdateProduct_PriceChangeRecordsHistory() {
        Product existing = Product.builder().id(1L).name("Laptop").description("Gaming Laptop")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU123").build();
        Product updatedDetails = Product.builder().id(1L).name("Laptop").description("Gaming Laptop V2")
                .price(new BigDecimal("120.00")).category("Tech").sku("SKU123").build();

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(any(Product.class))).thenReturn(updatedDetails);

        Product result = domainService.updateProduct(1L, updatedDetails);

        assertEquals("Gaming Laptop V2", result.getDescription());
        assertEquals(new BigDecimal("120.00"), result.getPrice());
        verify(repository).savePriceHistory(any(PriceHistory.class));
        verify(publisher).publishProductUpdated(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success_PublishesEvent() {
        when(repository.delete(1L)).thenReturn(true);

        domainService.deleteProduct(1L);

        verify(repository).delete(1L);
        verify(publisher).publishProductDeleted(1L);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsNotFoundException() {
        when(repository.delete(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> domainService.deleteProduct(99L));
        verify(publisher, never()).publishProductDeleted(any());
    }

    @Test
    void testGetProduct_NotFound_ThrowsNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> domainService.getProduct(99L, null));
    }

    @Test
    void testGetProductsByCategory_ReturnsList() {
        Product p = Product.builder().id(1L).name("Mouse").description("USB")
                .price(new BigDecimal("25.00")).category("Peripherals").sku("SKU-M").build();
        when(repository.findByCategory("Peripherals", 0, 10)).thenReturn(List.of(p));

        List<Product> result = domainService.getProductsByCategory("Peripherals", 0, 10, null);

        assertEquals(1, result.size());
        assertEquals("Mouse", result.get(0).getName());
    }

    @Test
    void testGetProductStock_NotFound_ThrowsNotFoundException() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> domainService.getProductStock(99L));
    }

    @Test
    void testSyncCurrentStock_UpdatesWhenProductExists() {
        Product product = Product.builder().id(1L).name("Laptop").price(new BigDecimal("100")).sku("S1").build();
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        domainService.syncCurrentStock(1L, 42);

        verify(repository).updateCurrentStock(1L, 42);
    }

    @Test
    void testSyncCurrentStock_DoesNothingWhenProductNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        domainService.syncCurrentStock(99L, 10);

        verify(repository, never()).updateCurrentStock(any(), any());
    }

    @Test
    void testSyncCurrentStock_NegativeStock_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> domainService.syncCurrentStock(1L, -5));
        verify(repository, never()).updateCurrentStock(any(), any());
    }

    @Test
    void testGetAllProducts_WithPagination_ReturnsList() {
        Product p = Product.builder().id(1L).name("Laptop").description("Gaming")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU1").currentStock(5).build();
        when(repository.findAll(0, 10)).thenReturn(List.of(p));

        List<Product> result = domainService.getAllProducts(0, 10, null);

        assertEquals(1, result.size());
        assertEquals("Laptop", result.get(0).getName());
        verify(repository).findAll(0, 10);
    }

    @Test
    void testGetAllProducts_WithCurrency_ConvertsPrice() {
        Product p = Product.builder().id(1L).name("Laptop").description("Gaming")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU1").currentStock(5).build();
        when(repository.findAll(0, 10)).thenReturn(List.of(p));
        when(exchangeRatePort.getExchangeRate("EUR")).thenReturn(new BigDecimal("0.90"));

        List<Product> result = domainService.getAllProducts(0, 10, "EUR");

        assertEquals(1, result.size());
        assertEquals(new BigDecimal("90.00"), result.get(0).getPrice());
        verify(exchangeRatePort).getExchangeRate("EUR");
    }

    @Test
    void testGetAllProducts_USDCurrency_DoesNotConvert() {
        Product p = Product.builder().id(1L).name("Laptop").description("Gaming")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU1").currentStock(5).build();
        when(repository.findAll(0, 10)).thenReturn(List.of(p));

        List<Product> result = domainService.getAllProducts(0, 10, "USD");

        assertEquals(new BigDecimal("100.00"), result.get(0).getPrice());
        verify(exchangeRatePort, never()).getExchangeRate(any());
    }

    @Test
    void testGetProductsByCategory_NoPagination_ReturnsList() {
        Product p = Product.builder().id(1L).name("Mouse").description("USB")
                .price(new BigDecimal("25.00")).category("Peripherals").sku("SKU-M").currentStock(10).build();
        when(repository.findByCategory("Peripherals")).thenReturn(List.of(p));

        List<Product> result = domainService.getProductsByCategory("Peripherals", null);

        assertEquals(1, result.size());
        verify(repository).findByCategory("Peripherals");
    }

    @Test
    void testGetPriceHistory_NoPagination_ReturnsList() {
        Product product = Product.builder().id(1L).name("Laptop").price(new BigDecimal("100")).sku("S1").build();
        PriceHistory ph = new PriceHistory(1L, 1L, new BigDecimal("100.00"), null);
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.findPriceHistoryByProductId(1L)).thenReturn(List.of(ph));

        List<PriceHistory> result = domainService.getPriceHistory(1L);

        assertEquals(1, result.size());
        verify(repository).findPriceHistoryByProductId(1L);
    }

    @Test
    void testGetProduct_NullCurrency_ReturnsOriginalPrice() {
        Product product = Product.builder().id(1L).name("Laptop").description("Gaming")
                .price(new BigDecimal("100.00")).category("Tech").sku("SKU1").currentStock(5).build();
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        Product result = domainService.getProduct(1L, null);

        assertEquals(new BigDecimal("100.00"), result.getPrice());
        verify(exchangeRatePort, never()).getExchangeRate(any());
    }
}

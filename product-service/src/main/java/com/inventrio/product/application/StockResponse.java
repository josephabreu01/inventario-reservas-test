package com.inventrio.product.application;

public record StockResponse(
    Long productId,
    Integer quantity
) {}

package com.inventrio.product.infrastructure.config;

import com.inventrio.product.domain.port.out.ExchangeRatePort;
import com.inventrio.product.domain.port.out.ProductEventPublisherPort;
import com.inventrio.product.domain.port.out.ProductRepositoryPort;
import com.inventrio.product.domain.service.ProductDomainService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class ProductServiceConfig {

    @Produces
    @ApplicationScoped
    public ProductDomainService productDomainService(
            ProductRepositoryPort repository,
            ProductEventPublisherPort publisher,
            ExchangeRatePort exchangeRatePort) {
        return new ProductDomainService(repository, publisher, exchangeRatePort);
    }
}

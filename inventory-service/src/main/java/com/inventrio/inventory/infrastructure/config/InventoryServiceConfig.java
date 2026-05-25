package com.inventrio.inventory.infrastructure.config;

import com.inventrio.inventory.domain.port.out.InventoryEventPublisherPort;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import com.inventrio.inventory.domain.service.InventoryDomainService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class InventoryServiceConfig {

    @Produces
    @ApplicationScoped
    public InventoryDomainService inventoryDomainService(
            InventoryRepositoryPort repository,
            InventoryEventPublisherPort publisher) {
        return new InventoryDomainService(repository, publisher);
    }
}

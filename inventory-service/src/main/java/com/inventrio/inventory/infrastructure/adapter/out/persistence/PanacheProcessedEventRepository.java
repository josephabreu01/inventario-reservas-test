package com.inventrio.inventory.infrastructure.adapter.out.persistence;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PanacheProcessedEventRepository implements PanacheRepositoryBase<ProcessedEventEntity, String> {
}

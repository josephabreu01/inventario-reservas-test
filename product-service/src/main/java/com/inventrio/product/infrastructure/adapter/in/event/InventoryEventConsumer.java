package com.inventrio.product.infrastructure.adapter.in.event;

import com.inventrio.product.domain.port.in.ProductUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class InventoryEventConsumer {

    private static final Logger LOG = Logger.getLogger(InventoryEventConsumer.class);

    private final ProductUseCase productUseCase;

    @Inject
    public InventoryEventConsumer(ProductUseCase productUseCase) {
        this.productUseCase = productUseCase;
    }

    @Incoming("inventory-events-in")
    @Transactional
    public void consume(InventoryAdjustedEvent event) {
        if (event.productId() == null || event.newQuantity() == null) {
            LOG.warnf("Received incomplete inventory event, skipping.");
            return;
        }
        LOG.infof("Updating local stock for product %d -> %d units", event.productId(), event.newQuantity());
        productUseCase.syncCurrentStock(event.productId(), event.newQuantity());
    }
}

package com.inventrio.inventory.infrastructure.adapter.in.event;

import com.inventrio.inventory.application.ProductSyncApplicationService;
import com.inventrio.inventory.domain.port.out.InventoryRepositoryPort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProductEventConsumer {

    private static final Logger LOG = Logger.getLogger(ProductEventConsumer.class);

    private final ProductSyncApplicationService productSyncService;
    private final InventoryRepositoryPort repositoryPort;

    @Inject
    public ProductEventConsumer(ProductSyncApplicationService productSyncService,
                                InventoryRepositoryPort repositoryPort) {
        this.productSyncService = productSyncService;
        this.repositoryPort = repositoryPort;
    }

    @Incoming("product-events-in")
    @Transactional
    public void consume(ProductEvent event) {
        LOG.infof("Received event %s with ID %s for Product %d", event.eventType(), event.eventId(), event.productId());

        if (repositoryPort.isEventProcessed(event.eventId())) {
            LOG.warnf("Event %s has already been processed. Skipping.", event.eventId());
            return;
        }

        try {
            routeEvent(event);
            repositoryPort.markEventAsProcessed(event.eventId());
            LOG.infof("Event %s successfully processed.", event.eventId());
        } catch (Exception e) {
            LOG.errorf(e, "Error processing event %s", event.eventId());
            throw e;
        }
    }

    private void routeEvent(ProductEvent event) {
        switch (event.eventType()) {
            case ProductEvent.CREATED ->
                productSyncService.onProductCreated(event.productId(), event.name(), event.price(), event.category());
            case ProductEvent.UPDATED ->
                productSyncService.onProductUpdated(event.productId(), event.name(), event.price(), event.category());
            case ProductEvent.DELETED ->
                productSyncService.onProductDeleted(event.productId());
            default ->
                LOG.infof("Event type %s skipped (no action required)", event.eventType());
        }
    }
}

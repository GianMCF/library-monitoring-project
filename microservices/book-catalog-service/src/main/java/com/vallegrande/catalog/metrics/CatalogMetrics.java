package com.vallegrande.catalog.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

@Component
public class CatalogMetrics {

    private final Counter catalogRequestsCounter;
    private final Counter booksCreatedCounter;
    private final Timer lookupTimer;

    public CatalogMetrics(
            Counter catalogRequestsCounter,
            Counter booksCreatedCounter,
            Timer lookupTimer
    ) {
        this.catalogRequestsCounter = catalogRequestsCounter;
        this.booksCreatedCounter = booksCreatedCounter;
        this.lookupTimer = lookupTimer;
    }

    public void incrementCatalogRequests() {
        catalogRequestsCounter.increment();
    }

    public void incrementBooksCreated() {
        booksCreatedCounter.increment();
    }

    public Timer getLookupTimer() {
        return lookupTimer;
    }
}
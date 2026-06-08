package com.vallegrande.catalog.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import com.vallegrande.catalog.service.BookService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter catalogRequestsCounter(MeterRegistry registry) {

        return Counter.builder("catalog_requests_total")
                .description("Total catalog requests")
                .register(registry);
    }

    @Bean
    public Counter booksCreatedCounter(MeterRegistry registry) {

        return Counter.builder("books_created_total")
                .description("Books created")
                .register(registry);
    }

    @Bean
    public Timer lookupTimer(MeterRegistry registry) {

        return Timer.builder("catalog_lookup_duration")
                .description("Catalog lookup duration")
                .register(registry);
    }

    @Bean
    public Gauge booksAvailableGauge(
            MeterRegistry registry,
            BookService service
    ) {

        return Gauge.builder(
                        "books_available",
                        service,
                        BookService::getTotalBooks
                )
                .description("Books available")
                .register(registry);
    }

    @Bean
    public Gauge artificialDelayGauge(
            MeterRegistry registry,
            BookService service
    ) {

        return Gauge.builder(
                        "catalog_response_delay_ms",
                        service,
                        BookService::getArtificialDelay
                )
                .description("Artificial response delay")
                .register(registry);
    }
}
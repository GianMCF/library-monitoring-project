package com.vallegrande.loan.config;

import com.vallegrande.loan.service.LoanService;
import io.micrometer.core.instrument.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean (name = "loanRequestsCounter")
    Counter loanRequestsCounter(MeterRegistry registry) {
        return Counter.builder("loan_requests_total")
                .register(registry);
    }

    @Bean (name = "loanFailuresCounter")
    Counter loanFailuresCounter(MeterRegistry registry) {
        return Counter.builder("loan_failures_total")
                .register(registry);
    }

    @Bean (name = "loanCreationTimer")
    Timer loanCreationTimer(MeterRegistry registry) {
        return Timer.builder("loan_creation_duration")
                .register(registry);
    }

    @Bean
    Gauge activeLoansGauge(
            MeterRegistry registry,
            LoanService service
    ) {

        return Gauge.builder(
                "active_loans",
                service,
                LoanService::getLoanCount
        ).register(registry);
    }
}
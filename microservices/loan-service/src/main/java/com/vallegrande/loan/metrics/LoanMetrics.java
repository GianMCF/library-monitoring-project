package com.vallegrande.loan.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class LoanMetrics {

    private final Counter requests;
    private final Counter failures;
    private final Timer loanTimer;

    public LoanMetrics(
            @Qualifier("loanRequestsCounter")
            Counter requests,

            @Qualifier("loanFailuresCounter")
            Counter failures,

            @Qualifier("loanCreationTimer")
            Timer loanTimer
    ) {
        this.requests = requests;
        this.failures = failures;
        this.loanTimer = loanTimer;
    }

    public void incrementRequests() {
        requests.increment();
    }

    public void incrementFailures() {
        failures.increment();
    }

    public Timer getLoanTimer() {
        return loanTimer;
    }
}
package com.vallegrande.loan.service;

import com.vallegrande.loan.metrics.LoanMetrics;
import com.vallegrande.loan.model.Book;
import com.vallegrande.loan.model.Loan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoanService {

    private final Map<Long, Loan> loans =
            new ConcurrentHashMap<>();

    private final WebClient catalogClient;
    private final LoanMetrics metrics;

    @Value("${catalog.timeout-seconds}")
    private long timeout;

    public LoanService(
            WebClient catalogClient,
            LoanMetrics metrics
    ) {
        this.catalogClient = catalogClient;
        this.metrics = metrics;
    }

    public ArrayList<Loan> getAllLoans() {
        return new ArrayList<>(loans.values());
    }

    public Loan getLoan(Long id) {
        return loans.get(id);
    }

    public Loan createLoan(Loan loan) {

        return metrics.getLoanTimer().record(() -> {

            metrics.incrementRequests();

            try {

                Book book =
                        catalogClient
                                .get()
                                .uri("/books/" + loan.getBookId())
                                .retrieve()
                                .bodyToMono(Book.class)
                                .timeout(Duration.ofSeconds(timeout))
                                .block();

                if (book == null) {

                    metrics.incrementFailures();

                    throw new RuntimeException(
                            "Book not found"
                    );
                }

                loans.put(
                        loan.getId(),
                        loan
                );

                return loan;

            } catch (Exception ex) {

                metrics.incrementFailures();

                throw new RuntimeException(
                        "Catalog service unavailable",
                        ex
                );
            }

        });
    }

    public void deleteLoan(Long id) {
        loans.remove(id);
    }

    public int getLoanCount() {
        return loans.size();
    }
}
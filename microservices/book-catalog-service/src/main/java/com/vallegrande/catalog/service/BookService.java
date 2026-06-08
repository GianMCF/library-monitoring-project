package com.vallegrande.catalog.service;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.vallegrande.catalog.model.Book;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vallegrande.catalog.metrics.CatalogMetrics;

@Service
public class BookService {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();

    private final CatalogMetrics metrics;

    @Value("${catalog.artificial-delay-ms}")
    private long artificialDelay;

    public BookService(CatalogMetrics metrics) {

        this.metrics = metrics;

        books.put(
                1L,
                new Book(
                        1L,
                        "Clean Code",
                        "Robert Martin",
                        10
                )
        );

        books.put(
                2L,
                new Book(
                        2L,
                        "Design Patterns",
                        "GoF",
                        5
                )
        );

        books.put(
                3L,
                new Book(
                        3L,
                        "Spring Boot",
                        "Craig Walls",
                        7
                )
        );
    }

    private void applyDelay() {

        if (artificialDelay <= 0) {
            return;
        }

        try {
            Thread.sleep(artificialDelay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ArrayList<Book> getAllBooks() {

        applyDelay();

        metrics.incrementCatalogRequests();

        return new ArrayList<>(books.values());
    }

    public Book getBook(Long id) {

        return metrics
                .getLookupTimer()
                .record(() -> {

                    applyDelay();

                    metrics.incrementCatalogRequests();

                    return books.get(id);
                });
    }

    public Book createBook(Book book) {

        applyDelay();

        books.put(book.getId(), book);

        metrics.incrementBooksCreated();

        return book;
    }

    public void deleteBook(Long id) {

        applyDelay();

        books.remove(id);
    }

    public int getTotalBooks() {
        return books.size();
    }

    public long getArtificialDelay() {
        return artificialDelay;
    }
}
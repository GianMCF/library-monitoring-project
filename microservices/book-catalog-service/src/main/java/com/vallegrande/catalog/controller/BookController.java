package com.vallegrande.catalog.controller;

import java.util.List;

import com.vallegrande.catalog.model.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vallegrande.catalog.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {

        return ResponseEntity.ok(
                service.getAllBooks()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBook(
            @PathVariable Long id
    ) {

        Book book = service.getBook(id);

        if (book == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(book);
    }

    @PostMapping
    public ResponseEntity<Book> createBook(
            @RequestBody Book book
    ) {

        return ResponseEntity.ok(
                service.createBook(book)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(
            @PathVariable Long id
    ) {

        service.deleteBook(id);

        return ResponseEntity.noContent().build();
    }
}
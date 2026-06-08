package com.vallegrande.catalog.model;

public class Book {

    private Long id;
    private String title;
    private String author;
    private Integer stock;

    public Book() {
    }

    public Book(Long id, String title, String author, Integer stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
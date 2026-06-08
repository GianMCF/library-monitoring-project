package com.vallegrande.loan.model;

public class Loan {

    private Long id;
    private Long bookId;
    private String borrower;

    public Loan() {
    }

    public Loan(Long id, Long bookId, String borrower) {
        this.id = id;
        this.bookId = bookId;
        this.borrower = borrower;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getBorrower() {
        return borrower;
    }

    public void setBorrower(String borrower) {
        this.borrower = borrower;
    }
}
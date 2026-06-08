package com.vallegrande.loan.controller;

import com.vallegrande.loan.model.Loan;
import com.vallegrande.loan.service.LoanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService service;

    public LoanController(
            LoanService service
    ) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Loan>> getAll() {

        return ResponseEntity.ok(
                service.getAllLoans()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Loan> getOne(
            @PathVariable Long id
    ) {

        Loan loan =
                service.getLoan(id);

        if (loan == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(loan);
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Loan loan
    ) {

        try {

            return ResponseEntity.ok(
                    service.createLoan(loan)
            );

        } catch (Exception ex) {

            return ResponseEntity
                    .internalServerError()
                    .body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.deleteLoan(id);

        return ResponseEntity.noContent().build();
    }
}
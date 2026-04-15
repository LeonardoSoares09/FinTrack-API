package com.leonardosoares.fintrack_api.controller;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.leonardosoares.fintrack_api.controller.dto.TransactionRequest;
import com.leonardosoares.fintrack_api.controller.dto.TransactionResponse;
import com.leonardosoares.fintrack_api.controller.dto.summary.SummaryResponse;
import com.leonardosoares.fintrack_api.model.enums.TransactionType;
import com.leonardosoares.fintrack_api.service.TransactionService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody TransactionRequest dto, HttpServletRequest request) {
        TransactionResponse transaction = transactionService.createTransaction(dto);

        URI location = ServletUriComponentsBuilder.fromRequestUri(request)
                .path("/{id}")
                .buildAndExpand(transaction.id())
                .toUri();
        return ResponseEntity.created(location).body(transaction);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll(@RequestParam(required = false) TransactionType type, @RequestParam(required = false) UUID categoryId) {
        return ResponseEntity.ok(transactionService.getAllTransactionsByUser(type, categoryId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id)); 
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(@PathVariable UUID id, @Valid @RequestBody TransactionRequest dto) {
        var c = transactionService.updateTransaction(id, dto);
        return ResponseEntity.ok(c);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary() {
        return ResponseEntity.ok(transactionService.getSummary());
    }
}
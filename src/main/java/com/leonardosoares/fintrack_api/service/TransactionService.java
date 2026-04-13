package com.leonardosoares.fintrack_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.leonardosoares.fintrack_api.controller.dto.TransactionRequest;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.model.Transaction;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;
import com.leonardosoares.fintrack_api.repository.TransactionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public Transaction createTransaction (TransactionRequest dto) {

        Category category = categoryRepository.findById(dto.categoryId())
        .orElseThrow(() -> new EntityNotFoundException("Category not found!"));

        Transaction t = new Transaction();
        t.setAmount(dto.amount());
        t.setType(dto.type());
        t.setDate(dto.date());
        t.setDescription(dto.description());
        t.setCategory(category);

        return transactionRepository.save(t);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransactionById(UUID id) {
        return transactionRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaction not found!"));
    }

    public Transaction updateTransaction(UUID id, Transaction transaction) {
        Transaction t = getTransactionById(id);
        t.setAmount(transaction.getAmount());
        t.setType(transaction.getType());
        t.setDate(transaction.getDate());
        t.setDescription(transaction.getDescription());
        t.setCategory(transaction.getCategory());
        //acho que do user nao teria como trocar ne

        return transactionRepository.save(t);
    }

    public void deleteTransaction(UUID id) {
        transactionRepository.deleteById(id);
    }
}
package com.leonardosoares.fintrack_api.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.leonardosoares.fintrack_api.config.exception.ResourceNotFoundException;
import com.leonardosoares.fintrack_api.controller.dto.TransactionRequest;
import com.leonardosoares.fintrack_api.controller.dto.TransactionResponse;
import com.leonardosoares.fintrack_api.controller.dto.summary.SummaryResponse;
import com.leonardosoares.fintrack_api.mapper.TransactionMapper;
import com.leonardosoares.fintrack_api.model.Category;
import com.leonardosoares.fintrack_api.model.Transaction;
import com.leonardosoares.fintrack_api.model.User;
import com.leonardosoares.fintrack_api.model.enums.TransactionType;
import com.leonardosoares.fintrack_api.repository.CategoryRepository;
import com.leonardosoares.fintrack_api.repository.TransactionRepository;
import com.leonardosoares.fintrack_api.repository.UserRepository;
import com.leonardosoares.fintrack_api.spec.TransactionSpecification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public TransactionResponse createTransaction (TransactionRequest dto) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(email));

        Category category = categoryRepository.findById(dto.categoryId())
        .orElseThrow(() -> new ResourceNotFoundException(email));

        Transaction t = new Transaction();
        t.setAmount(dto.amount());
        t.setType(dto.type());
        t.setDate(dto.date());
        t.setDescription(dto.description());
        t.setCategory(category);
        t.setUser(user);

        transactionRepository.save(t);

        TransactionResponse transactionResponse = transactionMapper.toResponse(t);

        return transactionResponse;
    }

    public List<TransactionResponse> getAllTransactionsByUser(TransactionType type, UUID categoryId) {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(email));

        Specification<Transaction> spec = Specification
            .where(TransactionSpecification.hasUser(user))
            .and(TransactionSpecification.hasType(type))
            .and(TransactionSpecification.hasCategoryId(categoryId));

        return transactionRepository.findAll(spec)
            .stream()
            .map(transaction -> transactionMapper.toResponse(transaction))
            .toList();
    }

    public TransactionResponse getTransactionById(UUID id) {
        Transaction t = transactionRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Transaction not found!"));

        return transactionMapper.toResponse(t);
    }

    public TransactionResponse updateTransaction(UUID id, TransactionRequest dto) {
        Transaction t = transactionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Transaction not found!"));

        Category c = categoryRepository.findById(dto.categoryId()).orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        t.setAmount(dto.amount());
        t.setType(dto.type());
        t.setDate(dto.date());
        t.setDescription(dto.description());
        t.setCategory(c);

        return transactionMapper.toResponse(transactionRepository.save(t));
    }

    public void deleteTransaction(UUID id) {
        transactionRepository.deleteById(id);
    }

    public SummaryResponse getSummary() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(email));

        BigDecimal totalDespesa = transactionRepository.sumByUserAndType(user, TransactionType.DESPESA);
        BigDecimal totalReceita = transactionRepository.sumByUserAndType(user, TransactionType.RECEITA);

        totalDespesa = totalDespesa != null ? totalDespesa : BigDecimal.ZERO;
        totalReceita = totalReceita != null ? totalReceita : BigDecimal.ZERO;

        BigDecimal total = totalReceita.subtract(totalDespesa);

        SummaryResponse summaryResponse = new SummaryResponse(totalReceita, totalDespesa, total);
        return summaryResponse;
    }
}
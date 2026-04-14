package com.leonardosoares.fintrack_api.mapper;

import org.springframework.stereotype.Component;

import com.leonardosoares.fintrack_api.controller.dto.TransactionResponse;
import com.leonardosoares.fintrack_api.model.Transaction;

@Component
public class TransactionMapper {
    
    public TransactionResponse toResponse(Transaction t) {
        return new TransactionResponse(
            t.getId(),
            t.getAmount(),
            t.getType(),
            t.getDate(),
            t.getCreatedAt(),
            t.getDescription(),
            t.getCategory().getId(),
            t.getUser().getId()
        );
    }
}

package com.leonardosoares.fintrack_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.leonardosoares.fintrack_api.model.enums.TransactionType;

public record TransactionRequest(
    BigDecimal amount,
    TransactionType type,
    LocalDate date,
    String description,
    UUID categoryId
) {
    
}

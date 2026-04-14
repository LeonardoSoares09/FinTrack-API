package com.leonardosoares.fintrack_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import com.leonardosoares.fintrack_api.model.enums.TransactionType;

public record TransactionResponse(
    UUID id,
    BigDecimal amount,
    TransactionType type,
    LocalDate date,
    LocalDateTime createdAt,
    String description,
    UUID categoryId,
    UUID userId
) {
    
}

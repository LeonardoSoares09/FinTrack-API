package com.leonardosoares.fintrack_api.controller.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;


import com.leonardosoares.fintrack_api.model.enums.TransactionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
    @NotBlank
    BigDecimal amount,
    @NotNull
    TransactionType type,
    @NotNull
    LocalDate date,
    @NotBlank
    String description,
    @NotNull
    UUID categoryId
) {
    
}

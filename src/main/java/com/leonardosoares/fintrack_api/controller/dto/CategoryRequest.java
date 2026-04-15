package com.leonardosoares.fintrack_api.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank
    String name,
    @NotBlank
    String description) {
} 

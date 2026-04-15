package com.leonardosoares.fintrack_api.controller.dto;

import java.util.UUID;

public record CategoryResponse(UUID id,
    String name,
    String description
) {
    
}

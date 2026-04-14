package com.leonardosoares.fintrack_api.controller.dto;

public record ErrorResponse(int status, String message, long timestamp) {
}

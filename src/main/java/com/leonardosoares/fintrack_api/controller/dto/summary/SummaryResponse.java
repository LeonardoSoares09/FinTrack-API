package com.leonardosoares.fintrack_api.controller.dto.summary;

import java.math.BigDecimal;

public record SummaryResponse(BigDecimal totalReceita, BigDecimal totalDespesas, BigDecimal saldo) {
    
}

package com.example.internshipapp.project.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProjectLotResponse(
        Long id,
        Long projectId,
        String designation,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal plannedAmount,
        boolean archived,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

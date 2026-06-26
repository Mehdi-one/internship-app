package com.example.internshipapp.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.ProjectType;

public record ProjectResponse(
        Long id,
        String reference,
        String title,
        String clientName,
        ProjectType projectType,
        BigDecimal amountHT,
        BigDecimal tvaRate,
        BigDecimal estimatedBudget,
        LocalDate startDate,
        LocalDate endDate,
        Integer executionDelayDays,
        String responsibleName,
        boolean archived,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

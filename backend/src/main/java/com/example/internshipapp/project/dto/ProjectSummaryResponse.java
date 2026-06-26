package com.example.internshipapp.project.dto;

import java.math.BigDecimal;

import com.example.internshipapp.common.enums.ProjectStatus;

public record ProjectSummaryResponse(
        Long projectId,
        String reference,
        String title,
        BigDecimal amountHT,
        BigDecimal estimatedBudget,
        BigDecimal totalExpenses,
        BigDecimal margin,
        BigDecimal marginRate,
        BigDecimal budgetConsumptionRate,
        ProjectStatus status) {
}

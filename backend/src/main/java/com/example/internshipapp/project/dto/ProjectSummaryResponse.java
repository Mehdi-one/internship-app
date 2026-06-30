package com.example.internshipapp.project.dto;

import java.math.BigDecimal;

import com.example.internshipapp.common.enums.ProjectStatus;

public record ProjectSummaryResponse(
        Long projectId,
        String reference,
        String title,
        BigDecimal awardedAmountHT,
        BigDecimal estimatedDryCost,
        BigDecimal directExpenses,
        BigDecimal laborCost,
        BigDecimal equipmentCost,
        BigDecimal totalExpenses,
        BigDecimal forecastMargin,
        BigDecimal forecastMarginRate,
        BigDecimal provisionalGrossMargin,
        BigDecimal provisionalGrossMarginRate,
        BigDecimal remainingBudget,
        BigDecimal budgetConsumptionRate,
        ProjectStatus status) {
}

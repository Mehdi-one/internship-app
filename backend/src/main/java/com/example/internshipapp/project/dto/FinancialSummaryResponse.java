package com.example.internshipapp.project.dto;

import java.math.BigDecimal;

public record FinancialSummaryResponse(
        Long projectId,
        BigDecimal budgetPrevisionnel,
        BigDecimal debourseTotal,
        BigDecimal debourseMainOeuvre,
        BigDecimal debourseParc,
        BigDecimal debourseDepenses,
        BigDecimal consommationBudget,
        BigDecimal alerteSeuil,
        boolean budgetAlert,
        boolean budgetCritique,
        BigDecimal montantHT,
        BigDecimal margeBrute,
        BigDecimal tauxMarge) {
}

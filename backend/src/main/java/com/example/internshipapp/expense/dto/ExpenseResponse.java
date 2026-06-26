package com.example.internshipapp.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;

public record ExpenseResponse(
        Long id,
        Long projectId,
        Long projectLotId,
        String projectLotDesignation,
        ExpenseCategory category,
        String description,
        BigDecimal amountHT,
        BigDecimal tvaRate,
        String supplierName,
        String invoiceNumber,
        LocalDate expenseDate,
        ExpenseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

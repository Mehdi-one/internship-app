package com.example.internshipapp.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;
import com.example.internshipapp.common.enums.ExpenseType;

public record ExpenseResponse(
        Long id,
        Long projectId,
        String projectReference,
        String projectTitle,
        Long projectLotId,
        String projectLotDesignation,
        ExpenseCategory category,
        ExpenseType expenseType,
        String description,
        BigDecimal amountHT,
        BigDecimal tvaRate,
        String supplierName,
        String invoiceNumber,
        LocalDate expenseDate,
        ExpenseStatus status,
        List<ExpenseDocumentResponse> documents,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

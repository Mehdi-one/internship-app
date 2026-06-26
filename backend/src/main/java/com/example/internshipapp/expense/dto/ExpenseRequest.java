package com.example.internshipapp.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ExpenseRequest(
        Long projectLotId,
        ExpenseCategory category,
        String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amountHT,
        BigDecimal tvaRate,
        String supplierName,
        String invoiceNumber,
        LocalDate expenseDate,
        ExpenseStatus status) {
}

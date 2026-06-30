package com.example.internshipapp.expense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.internshipapp.common.enums.ExpenseCategory;
import com.example.internshipapp.common.enums.ExpenseType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExpenseRequest(
        Long projectLotId,
        @NotNull ExpenseCategory category,
        @NotNull ExpenseType expenseType,
        @Size(max = 1000) String description,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amountHT,
        @DecimalMin(value = "0.00") @DecimalMax(value = "100.00") BigDecimal tvaRate,
        @Size(max = 150) String supplierName,
        @Size(max = 100) String invoiceNumber,
        @NotNull LocalDate expenseDate) {
}

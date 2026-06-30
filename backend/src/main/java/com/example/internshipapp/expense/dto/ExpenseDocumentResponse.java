package com.example.internshipapp.expense.dto;

import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ExpenseDocumentType;

public record ExpenseDocumentResponse(
        Long id,
        Long expenseId,
        ExpenseDocumentType documentType,
        String originalFileName,
        String contentType,
        long fileSize,
        LocalDateTime createdAt) {
}

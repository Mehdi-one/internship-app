package com.example.internshipapp.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmployeeCostHistoryResponse(
        Long id,
        Long employeeId,
        BigDecimal hourlyCost,
        LocalDate effectiveDate,
        LocalDateTime createdAt) {
}

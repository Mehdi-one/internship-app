package com.example.internshipapp.assignment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotNull;

public record EmployeeAssignmentRequest(
        @NotNull Long employeeId,
        @NotNull LocalDate assignmentDate,
        @NotNull @DecimalMin(value = "0.01") @DecimalMax(value = "24.00") BigDecimal hours) {
}

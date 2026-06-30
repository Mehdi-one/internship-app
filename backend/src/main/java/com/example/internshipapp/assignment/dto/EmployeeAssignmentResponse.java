package com.example.internshipapp.assignment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.AssignmentStatus;

public record EmployeeAssignmentResponse(
        Long id,
        Long projectId,
        Long employeeId,
        String employeeName,
        LocalDate assignmentDate,
        BigDecimal hours,
        BigDecimal hourlyCostSnapshot,
        BigDecimal totalCost,
        AssignmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

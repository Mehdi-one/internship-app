package com.example.internshipapp.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ContractType;
import com.example.internshipapp.common.enums.EmployeeStatus;

public record EmployeeResponse(
        Long id,
        String registrationNumber,
        String fullName,
        String qualification,
        ContractType contractType,
        BigDecimal hourlyCost,
        EmployeeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

package com.example.internshipapp.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.example.internshipapp.common.enums.ContractType;
import com.example.internshipapp.common.enums.EmployeeStatus;

public record EmployeeDetailResponse(
        Long id,
        String matricule,
        String fullName,
        String qualification,
        ContractType contractType,
        BigDecimal hourlyCost,
        EmployeeStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<EmployeeCostHistoryResponse> costHistory) {
}

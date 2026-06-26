package com.example.internshipapp.employee.dto;

import java.math.BigDecimal;

import com.example.internshipapp.common.enums.ContractType;
import com.example.internshipapp.common.enums.EmployeeStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmployeeRequest(
        @NotBlank String registrationNumber,
        @NotBlank String fullName,
        @NotBlank String qualification,
        @NotNull ContractType contractType,
        @NotNull @DecimalMin(value = "0.01") BigDecimal hourlyCost,
        EmployeeStatus status) {
}

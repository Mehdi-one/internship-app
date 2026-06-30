package com.example.internshipapp.project.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;

public record ProjectLotRequest(
        @NotBlank String designation,
        @DecimalMin(value = "0.00") BigDecimal quantity,
        @DecimalMin(value = "0.00") BigDecimal unitPrice,
        @DecimalMin(value = "0.01") BigDecimal plannedAmount) {
}

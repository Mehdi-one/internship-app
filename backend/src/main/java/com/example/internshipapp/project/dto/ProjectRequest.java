package com.example.internshipapp.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.ProjectType;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectRequest(
        @NotBlank String reference,
        @NotBlank String title,
        @NotBlank String contractingAuthority,
        @NotNull ProjectType projectType,
        @NotNull @DecimalMin(value = "0.01") BigDecimal awardedAmountHT,
        @NotNull @DecimalMin(value = "0.00") @DecimalMax(value = "100.00") BigDecimal tvaRate,
        @NotNull @DecimalMin(value = "0.01") BigDecimal estimatedDryCost,
        LocalDate notificationOrderDate,
        @Min(1) Integer executionDelayDays,
        @NotBlank String responsibleUserReference,
        ProjectStatus status) {
}

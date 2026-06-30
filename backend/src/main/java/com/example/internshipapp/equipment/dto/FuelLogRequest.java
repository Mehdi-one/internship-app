package com.example.internshipapp.equipment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FuelLogRequest(
        @NotNull LocalDate date,
        @NotNull @DecimalMin(value = "0.01") BigDecimal liters,
        @NotNull @DecimalMin(value = "0.01") BigDecimal costPerLiter,
        @DecimalMin(value = "0.00") BigDecimal mileageOrHours,
        @Size(max = 2000) String notes) {
}

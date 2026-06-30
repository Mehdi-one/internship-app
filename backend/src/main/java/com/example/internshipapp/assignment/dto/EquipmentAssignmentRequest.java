package com.example.internshipapp.assignment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EquipmentAssignmentRequest(
        @NotNull Long equipmentId,
        @NotNull LocalDate assignmentDate,
        @NotNull @DecimalMin(value = "0.01") BigDecimal usageQuantity,
        @DecimalMin(value = "0.00") BigDecimal fuelCost,
        @DecimalMin(value = "0.00") BigDecimal maintenanceCost,
        @DecimalMin(value = "0.00") BigDecimal transportCost) {
}

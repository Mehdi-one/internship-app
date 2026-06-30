package com.example.internshipapp.equipment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.EquipmentType;
import com.example.internshipapp.common.enums.UsageCostType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EquipmentRequest(
        @NotBlank String reference,
        @NotNull EquipmentType type,
        @NotBlank String brandModel,
        @DecimalMin(value = "0.00") BigDecimal acquisitionCost,
        @NotNull UsageCostType usageCostType,
        @NotNull @DecimalMin(value = "0.01") BigDecimal usageCost,
        @DecimalMin(value = "0.00") BigDecimal fuelConsumption,
        @DecimalMin(value = "0.00") BigDecimal maintenanceCost,
        @DecimalMin(value = "0.00") BigDecimal insuranceCost,
        LocalDate nextMaintenanceDate,
        LocalDate insuranceExpiryDate,
        EquipmentStatus status) {
}

package com.example.internshipapp.equipment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.EquipmentType;
import com.example.internshipapp.common.enums.UsageCostType;

public record EquipmentResponse(
        Long id,
        String reference,
        EquipmentType type,
        String brandModel,
        BigDecimal acquisitionCost,
        UsageCostType usageCostType,
        BigDecimal usageCost,
        BigDecimal fuelConsumption,
        BigDecimal maintenanceCost,
        BigDecimal insuranceCost,
        EquipmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

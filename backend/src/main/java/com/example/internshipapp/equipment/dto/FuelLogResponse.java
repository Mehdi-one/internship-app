package com.example.internshipapp.equipment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record FuelLogResponse(
        Long id,
        Long equipmentId,
        LocalDate date,
        BigDecimal liters,
        BigDecimal costPerLiter,
        BigDecimal totalCost,
        BigDecimal mileageOrHours,
        String notes,
        LocalDateTime createdAt) {
}

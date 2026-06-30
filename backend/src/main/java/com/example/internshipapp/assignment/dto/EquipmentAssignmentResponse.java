package com.example.internshipapp.assignment.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.UsageCostType;

public record EquipmentAssignmentResponse(
        Long id,
        Long projectId,
        Long equipmentId,
        String equipmentReference,
        String equipmentName,
        LocalDate assignmentDate,
        BigDecimal usageQuantity,
        UsageCostType usageCostType,
        BigDecimal usageCostSnapshot,
        BigDecimal fuelCost,
        BigDecimal maintenanceCost,
        BigDecimal transportCost,
        BigDecimal totalCost,
        AssignmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

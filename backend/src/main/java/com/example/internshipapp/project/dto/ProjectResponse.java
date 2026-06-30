package com.example.internshipapp.project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.ProjectType;

public record ProjectResponse(
        Long id,
        String reference,
        String title,
        String contractingAuthority,
        ProjectType projectType,
        BigDecimal awardedAmountHT,
        BigDecimal tvaRate,
        BigDecimal estimatedDryCost,
        LocalDate notificationOrderDate,
        Integer executionDelayDays,
        LocalDate plannedEndDate,
        String responsibleUserReference,
        boolean archived,
        ProjectStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

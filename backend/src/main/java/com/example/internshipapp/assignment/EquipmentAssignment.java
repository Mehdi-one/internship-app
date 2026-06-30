package com.example.internshipapp.assignment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.UsageCostType;
import com.example.internshipapp.equipment.Equipment;
import com.example.internshipapp.project.Project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipment_assignments")
public class EquipmentAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(nullable = false)
    private LocalDate assignmentDate;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal usageQuantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsageCostType usageCostType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal usageCostSnapshot;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal fuelCost;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal maintenanceCost;

    @Column(precision = 15, scale = 2)
    private BigDecimal transportCost;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssignmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (status == null) {
            status = AssignmentStatus.DRAFT;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }

    public LocalDate getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDate assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public BigDecimal getUsageQuantity() {
        return usageQuantity;
    }

    public void setUsageQuantity(BigDecimal usageQuantity) {
        this.usageQuantity = usageQuantity;
    }

    public UsageCostType getUsageCostType() {
        return usageCostType;
    }

    public void setUsageCostType(UsageCostType usageCostType) {
        this.usageCostType = usageCostType;
    }

    public BigDecimal getUsageCostSnapshot() {
        return usageCostSnapshot;
    }

    public void setUsageCostSnapshot(BigDecimal usageCostSnapshot) {
        this.usageCostSnapshot = usageCostSnapshot;
    }

    public BigDecimal getFuelCost() {
        return fuelCost;
    }

    public void setFuelCost(BigDecimal fuelCost) {
        this.fuelCost = fuelCost;
    }

    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public BigDecimal getTransportCost() {
        return transportCost;
    }

    public void setTransportCost(BigDecimal transportCost) {
        this.transportCost = transportCost;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(BigDecimal totalCost) {
        this.totalCost = totalCost;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

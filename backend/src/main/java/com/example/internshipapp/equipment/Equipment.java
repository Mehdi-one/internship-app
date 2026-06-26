package com.example.internshipapp.equipment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.EquipmentType;
import com.example.internshipapp.common.enums.UsageCostType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentType type;

    @Column(nullable = false)
    private String brandModel;

    @Column(precision = 15, scale = 2)
    private BigDecimal acquisitionCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UsageCostType usageCostType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal usageCost;

    @Column(precision = 15, scale = 2)
    private BigDecimal fuelConsumption;

    @Column(precision = 15, scale = 2)
    private BigDecimal maintenanceCost;

    @Column(precision = 15, scale = 2)
    private BigDecimal insuranceCost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;

        if (type == null) {
            type = EquipmentType.OTHER;
        }

        if (usageCostType == null) {
            usageCostType = UsageCostType.HOURLY;
        }

        if (status == null) {
            status = EquipmentStatus.AVAILABLE;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public EquipmentType getType() {
        return type;
    }

    public void setType(EquipmentType type) {
        this.type = type;
    }

    public String getBrandModel() {
        return brandModel;
    }

    public void setBrandModel(String brandModel) {
        this.brandModel = brandModel;
    }

    public BigDecimal getAcquisitionCost() {
        return acquisitionCost;
    }

    public void setAcquisitionCost(BigDecimal acquisitionCost) {
        this.acquisitionCost = acquisitionCost;
    }

    public UsageCostType getUsageCostType() {
        return usageCostType;
    }

    public void setUsageCostType(UsageCostType usageCostType) {
        this.usageCostType = usageCostType;
    }

    public BigDecimal getUsageCost() {
        return usageCost;
    }

    public void setUsageCost(BigDecimal usageCost) {
        this.usageCost = usageCost;
    }

    public BigDecimal getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(BigDecimal fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public BigDecimal getMaintenanceCost() {
        return maintenanceCost;
    }

    public void setMaintenanceCost(BigDecimal maintenanceCost) {
        this.maintenanceCost = maintenanceCost;
    }

    public BigDecimal getInsuranceCost() {
        return insuranceCost;
    }

    public void setInsuranceCost(BigDecimal insuranceCost) {
        this.insuranceCost = insuranceCost;
    }

    public EquipmentStatus getStatus() {
        return status;
    }

    public void setStatus(EquipmentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

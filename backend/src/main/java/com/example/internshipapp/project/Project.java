package com.example.internshipapp.project;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.ProjectType;

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
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String reference;

    @Column(nullable = false)
    private String title;

    @Column(name = "client_name")
    private String contractingAuthority;

    @Enumerated(EnumType.STRING)
    private ProjectType projectType;

    @Column(name = "amountht", nullable = false, precision = 15, scale = 2)
    private BigDecimal awardedAmountHT;

    @Column(precision = 5, scale = 2)
    private BigDecimal tvaRate;

    @Column(name = "estimated_budget", nullable = false, precision = 15, scale = 2)
    private BigDecimal estimatedDryCost;

    @Column(name = "start_date")
    private LocalDate notificationOrderDate;

    @Column(name = "end_date")
    private LocalDate plannedEndDate;

    private Integer executionDelayDays;

    @Column(name = "responsible_name")
    private String responsibleUserReference;

    private Boolean archived;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

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
            status = ProjectStatus.PROSPECT;
        }

        if (archived == null) {
            archived = false;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContractingAuthority() {
        return contractingAuthority;
    }

    public void setContractingAuthority(String contractingAuthority) {
        this.contractingAuthority = contractingAuthority;
    }

    public ProjectType getProjectType() {
        return projectType;
    }

    public void setProjectType(ProjectType projectType) {
        this.projectType = projectType;
    }

    public BigDecimal getAwardedAmountHT() {
        return awardedAmountHT;
    }

    public void setAwardedAmountHT(BigDecimal awardedAmountHT) {
        this.awardedAmountHT = awardedAmountHT;
    }

    public BigDecimal getTvaRate() {
        return tvaRate;
    }

    public void setTvaRate(BigDecimal tvaRate) {
        this.tvaRate = tvaRate;
    }

    public BigDecimal getEstimatedDryCost() {
        return estimatedDryCost;
    }

    public void setEstimatedDryCost(BigDecimal estimatedDryCost) {
        this.estimatedDryCost = estimatedDryCost;
    }

    public LocalDate getNotificationOrderDate() {
        return notificationOrderDate;
    }

    public void setNotificationOrderDate(LocalDate notificationOrderDate) {
        this.notificationOrderDate = notificationOrderDate;
    }

    public LocalDate getPlannedEndDate() {
        return plannedEndDate;
    }

    public void setPlannedEndDate(LocalDate plannedEndDate) {
        this.plannedEndDate = plannedEndDate;
    }

    public Integer getExecutionDelayDays() {
        return executionDelayDays;
    }

    public void setExecutionDelayDays(Integer executionDelayDays) {
        this.executionDelayDays = executionDelayDays;
    }

    public String getResponsibleUserReference() {
        return responsibleUserReference;
    }

    public void setResponsibleUserReference(String responsibleUserReference) {
        this.responsibleUserReference = responsibleUserReference;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

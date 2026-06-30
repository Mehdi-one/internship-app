package com.example.internshipapp.assignment;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.assignment.dto.EquipmentAssignmentRequest;
import com.example.internshipapp.assignment.dto.EquipmentAssignmentResponse;
import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.equipment.Equipment;
import com.example.internshipapp.equipment.EquipmentRepository;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectService;
import com.example.internshipapp.common.enums.ProjectStatus;

@Service
public class EquipmentAssignmentService {

    private final EquipmentAssignmentRepository equipmentAssignmentRepository;
    private final EquipmentRepository equipmentRepository;
    private final ProjectService projectService;

    public EquipmentAssignmentService(
            EquipmentAssignmentRepository equipmentAssignmentRepository,
            EquipmentRepository equipmentRepository,
            ProjectService projectService) {
        this.equipmentAssignmentRepository = equipmentAssignmentRepository;
        this.equipmentRepository = equipmentRepository;
        this.projectService = projectService;
    }

    @Transactional
    public EquipmentAssignmentResponse create(Long projectId, EquipmentAssignmentRequest request) {
        Project project = projectService.getProject(projectId);
        Equipment equipment = getEquipment(request.equipmentId());
        validateProject(project);
        validateEquipment(equipment);
        validateAssignmentDate(project, request.assignmentDate());
        validateDuplicate(equipment.getId(), request.assignmentDate(), null);

        EquipmentAssignment assignment = new EquipmentAssignment();
        assignment.setProject(project);
        assignment.setEquipment(equipment);
        fillAssignment(assignment, equipment, request);
        assignment.setStatus(AssignmentStatus.DRAFT);

        return toResponse(equipmentAssignmentRepository.save(assignment));
    }

    @Transactional
    public EquipmentAssignmentResponse update(Long id, EquipmentAssignmentRequest request) {
        EquipmentAssignment assignment = getAssignment(id);
        requireDraft(assignment);

        Project project = assignment.getProject();
        Equipment equipment = getEquipment(request.equipmentId());
        validateProject(project);
        validateEquipment(equipment);
        validateAssignmentDate(project, request.assignmentDate());
        validateDuplicate(equipment.getId(), request.assignmentDate(), id);

        assignment.setEquipment(equipment);
        fillAssignment(assignment, equipment, request);
        return toResponse(equipmentAssignmentRepository.save(assignment));
    }

    @Transactional(readOnly = true)
    public List<EquipmentAssignmentResponse> findByProject(Long projectId) {
        projectService.getProject(projectId);

        return equipmentAssignmentRepository.findByProjectIdOrderByAssignmentDateDesc(projectId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public EquipmentAssignmentResponse validate(Long id) {
        EquipmentAssignment assignment = getAssignment(id);
        requireDraft(assignment);
        validateProject(assignment.getProject());
        validateEquipment(assignment.getEquipment());
        assignment.setStatus(AssignmentStatus.VALIDATED);
        return toResponse(equipmentAssignmentRepository.save(assignment));
    }

    @Transactional
    public EquipmentAssignmentResponse cancel(Long id) {
        EquipmentAssignment assignment = getAssignment(id);
        if (assignment.getStatus() == AssignmentStatus.CANCELLED) {
            throw new IllegalArgumentException("A cancelled assignment cannot be cancelled again");
        }
        assignment.setStatus(AssignmentStatus.CANCELLED);
        return toResponse(equipmentAssignmentRepository.save(assignment));
    }

    private Equipment getEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
    }

    private EquipmentAssignment getAssignment(Long id) {
        return equipmentAssignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment assignment not found"));
    }

    private void fillAssignment(
            EquipmentAssignment assignment,
            Equipment equipment,
            EquipmentAssignmentRequest request) {
        BigDecimal fuelCost = valueOrZero(request.fuelCost());
        BigDecimal maintenanceCost = valueOrZero(request.maintenanceCost());
        BigDecimal transportCost = valueOrZero(request.transportCost());
        BigDecimal usageCostSnapshot = equipment.getUsageCost();
        BigDecimal usageCost = request.usageQuantity().multiply(usageCostSnapshot);

        assignment.setAssignmentDate(request.assignmentDate());
        assignment.setUsageQuantity(request.usageQuantity());
        assignment.setUsageCostType(equipment.getUsageCostType());
        assignment.setUsageCostSnapshot(usageCostSnapshot);
        assignment.setFuelCost(fuelCost);
        assignment.setMaintenanceCost(maintenanceCost);
        assignment.setTransportCost(transportCost);
        assignment.setTotalCost(usageCost.add(fuelCost).add(maintenanceCost).add(transportCost));
    }

    private void validateProject(Project project) {
        if (Boolean.TRUE.equals(project.getArchived()) || project.getStatus() != ProjectStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Assignments are only allowed on an active project in progress");
        }
    }

    private void validateEquipment(Equipment equipment) {
        if (equipment.getStatus() == EquipmentStatus.REFORMED
                || equipment.getStatus() == EquipmentStatus.MAINTENANCE) {
            throw new IllegalArgumentException("Cannot assign unavailable equipment");
        }
        if (equipment.getUsageCost() == null || equipment.getUsageCost().signum() <= 0) {
            throw new IllegalArgumentException("Equipment usage cost must be configured before assignment");
        }
    }

    private void requireDraft(EquipmentAssignment assignment) {
        if (assignment.getStatus() != AssignmentStatus.DRAFT) {
            throw new IllegalArgumentException("Only a draft assignment can be modified or validated");
        }
    }

    private void validateDuplicate(Long equipmentId, LocalDate date, Long currentId) {
        boolean exists = currentId == null
                ? equipmentAssignmentRepository.existsByEquipmentIdAndAssignmentDateAndStatusNot(
                        equipmentId, date, AssignmentStatus.CANCELLED)
                : equipmentAssignmentRepository.existsByEquipmentIdAndAssignmentDateAndStatusNotAndIdNot(
                        equipmentId, date, AssignmentStatus.CANCELLED, currentId);
        if (exists) {
            throw new IllegalArgumentException("This equipment is already assigned to another project on this date");
        }
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void validateAssignmentDate(Project project, LocalDate assignmentDate) {
        if (project.getNotificationOrderDate() != null && assignmentDate.isBefore(project.getNotificationOrderDate())) {
            throw new IllegalArgumentException("Assignment date cannot be before project start date");
        }

        if (project.getPlannedEndDate() != null && assignmentDate.isAfter(project.getPlannedEndDate())) {
            throw new IllegalArgumentException("Assignment date cannot be after project end date");
        }
    }

    private EquipmentAssignmentResponse toResponse(EquipmentAssignment assignment) {
        return new EquipmentAssignmentResponse(
                assignment.getId(),
                assignment.getProject().getId(),
                assignment.getEquipment().getId(),
                assignment.getEquipment().getReference(),
                assignment.getEquipment().getBrandModel(),
                assignment.getAssignmentDate(),
                assignment.getUsageQuantity(),
                assignment.getUsageCostType(),
                assignment.getUsageCostSnapshot(),
                assignment.getFuelCost(),
                assignment.getMaintenanceCost(),
                assignment.getTransportCost(),
                assignment.getTotalCost(),
                assignment.getStatus(),
                assignment.getCreatedAt(),
                assignment.getUpdatedAt());
    }
}

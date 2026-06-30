package com.example.internshipapp.assignment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.internshipapp.assignment.dto.EquipmentAssignmentRequest;
import com.example.internshipapp.assignment.dto.EquipmentAssignmentResponse;
import com.example.internshipapp.common.enums.AssignmentStatus;
import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.ProjectStatus;
import com.example.internshipapp.common.enums.UsageCostType;
import com.example.internshipapp.equipment.Equipment;
import com.example.internshipapp.equipment.EquipmentRepository;
import com.example.internshipapp.project.Project;
import com.example.internshipapp.project.ProjectService;

class EquipmentAssignmentServiceTest {

    private EquipmentAssignmentRepository assignmentRepository;
    private EquipmentRepository equipmentRepository;
    private ProjectService projectService;
    private EquipmentAssignmentService service;

    @BeforeEach
    void setUp() {
        assignmentRepository = mock(EquipmentAssignmentRepository.class);
        equipmentRepository = mock(EquipmentRepository.class);
        projectService = mock(ProjectService.class);
        service = new EquipmentAssignmentService(assignmentRepository, equipmentRepository, projectService);
    }

    @Test
    void createsPointageWithUsageCostSnapshotAndImputedCosts() {
        LocalDate date = LocalDate.of(2026, 7, 15);
        Project project = activeProject(1L);
        Equipment equipment = availableEquipment(2L, new BigDecimal("300.00"));
        EquipmentAssignmentRequest request = new EquipmentAssignmentRequest(
                2L, date, new BigDecimal("3.00"),
                new BigDecimal("100.00"), new BigDecimal("50.00"), new BigDecimal("25.00"));

        when(projectService.getProject(1L)).thenReturn(project);
        when(equipmentRepository.findById(2L)).thenReturn(Optional.of(equipment));
        when(assignmentRepository.save(any(EquipmentAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        EquipmentAssignmentResponse response = service.create(1L, request);

        assertThat(response.usageCostSnapshot()).isEqualByComparingTo("300.00");
        assertThat(response.totalCost()).isEqualByComparingTo("1075.00");
        assertThat(response.status()).isEqualTo(AssignmentStatus.DRAFT);
        verify(assignmentRepository).existsByEquipmentIdAndAssignmentDateAndStatusNot(
                2L, date, AssignmentStatus.CANCELLED);
    }

    @Test
    void rejectsEquipmentAlreadyPointedOnAnotherProjectTheSameDay() {
        LocalDate date = LocalDate.of(2026, 7, 15);
        Project project = activeProject(1L);
        Equipment equipment = availableEquipment(2L, new BigDecimal("300.00"));
        when(projectService.getProject(1L)).thenReturn(project);
        when(equipmentRepository.findById(2L)).thenReturn(Optional.of(equipment));
        when(assignmentRepository.existsByEquipmentIdAndAssignmentDateAndStatusNot(
                2L, date, AssignmentStatus.CANCELLED)).thenReturn(true);

        EquipmentAssignmentRequest request = new EquipmentAssignmentRequest(
                2L, date, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);

        assertThatThrownBy(() -> service.create(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already assigned to another project");
    }

    private Project activeProject(Long id) {
        Project project = mock(Project.class);
        when(project.getId()).thenReturn(id);
        when(project.getArchived()).thenReturn(false);
        when(project.getStatus()).thenReturn(ProjectStatus.IN_PROGRESS);
        return project;
    }

    private Equipment availableEquipment(Long id, BigDecimal usageCost) {
        Equipment equipment = mock(Equipment.class);
        when(equipment.getId()).thenReturn(id);
        when(equipment.getReference()).thenReturn("MAT-TEST");
        when(equipment.getBrandModel()).thenReturn("Materiel test");
        when(equipment.getStatus()).thenReturn(EquipmentStatus.AVAILABLE);
        when(equipment.getUsageCostType()).thenReturn(UsageCostType.HOURLY);
        when(equipment.getUsageCost()).thenReturn(usageCost);
        return equipment;
    }
}

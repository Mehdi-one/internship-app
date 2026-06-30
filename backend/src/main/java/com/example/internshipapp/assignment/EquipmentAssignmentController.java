package com.example.internshipapp.assignment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.example.internshipapp.assignment.dto.EquipmentAssignmentRequest;
import com.example.internshipapp.assignment.dto.EquipmentAssignmentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class EquipmentAssignmentController {

    private final EquipmentAssignmentService equipmentAssignmentService;

    public EquipmentAssignmentController(EquipmentAssignmentService equipmentAssignmentService) {
        this.equipmentAssignmentService = equipmentAssignmentService;
    }

    @PostMapping("/projects/{projectId}/equipment-assignments")
    public EquipmentAssignmentResponse create(
            @PathVariable Long projectId,
            @Valid @RequestBody EquipmentAssignmentRequest request) {
        return equipmentAssignmentService.create(projectId, request);
    }

    @GetMapping("/projects/{projectId}/equipment-assignments")
    public List<EquipmentAssignmentResponse> findByProject(@PathVariable Long projectId) {
        return equipmentAssignmentService.findByProject(projectId);
    }

    @PutMapping("/equipment-assignments/{id}")
    public EquipmentAssignmentResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentAssignmentRequest request) {
        return equipmentAssignmentService.update(id, request);
    }

    @PatchMapping("/equipment-assignments/{id}/validate")
    public EquipmentAssignmentResponse validate(@PathVariable Long id) {
        return equipmentAssignmentService.validate(id);
    }

    @PatchMapping("/equipment-assignments/{id}/cancel")
    public EquipmentAssignmentResponse cancel(@PathVariable Long id) {
        return equipmentAssignmentService.cancel(id);
    }
}

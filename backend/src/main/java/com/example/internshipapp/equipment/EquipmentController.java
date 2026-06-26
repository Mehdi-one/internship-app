package com.example.internshipapp.equipment;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.equipment.dto.EquipmentRequest;
import com.example.internshipapp.equipment.dto.EquipmentResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @PostMapping
    public EquipmentResponse create(@Valid @RequestBody EquipmentRequest request) {
        return equipmentService.create(request);
    }

    @GetMapping
    public List<EquipmentResponse> findAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) EquipmentStatus status) {
        return equipmentService.findAll(search, status);
    }

    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return equipmentService.findById(id);
    }

    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id, @Valid @RequestBody EquipmentRequest request) {
        return equipmentService.update(id, request);
    }

    @PatchMapping("/{id}/reform")
    public EquipmentResponse reform(@PathVariable Long id) {
        return equipmentService.reform(id);
    }
}

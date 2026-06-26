package com.example.internshipapp.equipment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.EquipmentType;
import com.example.internshipapp.common.enums.UsageCostType;
import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.equipment.dto.EquipmentRequest;
import com.example.internshipapp.equipment.dto.EquipmentResponse;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public EquipmentService(EquipmentRepository equipmentRepository) {
        this.equipmentRepository = equipmentRepository;
    }

    @Transactional
    public EquipmentResponse create(EquipmentRequest request) {
        if (equipmentRepository.existsByReference(request.reference())) {
            throw new IllegalArgumentException("Equipment reference already exists");
        }

        Equipment equipment = new Equipment();
        fillEquipment(equipment, request);
        return toResponse(equipmentRepository.save(equipment));
    }

    @Transactional(readOnly = true)
    public List<EquipmentResponse> findAll(String search, EquipmentStatus status) {
        String normalizedSearch = search == null || search.isBlank() ? null : search.trim();
        List<Equipment> equipment;

        if (normalizedSearch == null && status == null) {
            equipment = equipmentRepository.findAllByOrderByUpdatedAtDesc();
        } else if (normalizedSearch == null) {
            equipment = equipmentRepository.findByStatusOrderByUpdatedAtDesc(status);
        } else if (status == null) {
            equipment = equipmentRepository.search(normalizedSearch);
        } else {
            equipment = equipmentRepository.searchByStatus(normalizedSearch, status);
        }

        return equipment.stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public EquipmentResponse findById(Long id) {
        return toResponse(getEquipment(id));
    }

    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest request) {
        Equipment equipment = getEquipment(id);

        equipmentRepository.findByReference(request.reference())
                .filter(existingEquipment -> !existingEquipment.getId().equals(id))
                .ifPresent(existingEquipment -> {
                    throw new IllegalArgumentException("Equipment reference already exists");
                });

        fillEquipment(equipment, request);
        return toResponse(equipmentRepository.save(equipment));
    }

    @Transactional
    public EquipmentResponse reform(Long id) {
        Equipment equipment = getEquipment(id);
        equipment.setStatus(EquipmentStatus.REFORMED);
        return toResponse(equipmentRepository.save(equipment));
    }

    private Equipment getEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
    }

    private void fillEquipment(Equipment equipment, EquipmentRequest request) {
        equipment.setReference(request.reference());
        equipment.setType(request.type() == null ? EquipmentType.OTHER : request.type());
        equipment.setBrandModel(request.brandModel());
        equipment.setAcquisitionCost(request.acquisitionCost());
        equipment.setUsageCostType(request.usageCostType() == null ? UsageCostType.HOURLY : request.usageCostType());
        equipment.setUsageCost(request.usageCost());
        equipment.setFuelConsumption(request.fuelConsumption());
        equipment.setMaintenanceCost(request.maintenanceCost());
        equipment.setInsuranceCost(request.insuranceCost());
        equipment.setStatus(request.status() == null ? EquipmentStatus.AVAILABLE : request.status());
    }

    private EquipmentResponse toResponse(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getReference(),
                equipment.getType(),
                equipment.getBrandModel(),
                equipment.getAcquisitionCost(),
                equipment.getUsageCostType(),
                equipment.getUsageCost(),
                equipment.getFuelConsumption(),
                equipment.getMaintenanceCost(),
                equipment.getInsuranceCost(),
                equipment.getStatus(),
                equipment.getCreatedAt(),
                equipment.getUpdatedAt());
    }
}

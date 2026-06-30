package com.example.internshipapp.equipment;

import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.equipment.dto.FuelLogRequest;
import com.example.internshipapp.equipment.dto.FuelLogResponse;

@Service
public class FuelLogService {

    private final EquipmentRepository equipmentRepository;
    private final FuelLogRepository fuelLogRepository;

    public FuelLogService(EquipmentRepository equipmentRepository, FuelLogRepository fuelLogRepository) {
        this.equipmentRepository = equipmentRepository;
        this.fuelLogRepository = fuelLogRepository;
    }

    @Transactional
    public FuelLogResponse addFuelLog(Long equipmentId, FuelLogRequest request) {
        Equipment equipment = getEquipment(equipmentId);
        FuelLog fuelLog = new FuelLog();
        fuelLog.setEquipment(equipment);
        fuelLog.setDate(request.date());
        fuelLog.setLiters(request.liters());
        fuelLog.setCostPerLiter(request.costPerLiter());
        fuelLog.setTotalCost(request.liters().multiply(request.costPerLiter()).setScale(2, RoundingMode.HALF_UP));
        fuelLog.setMileageOrHours(request.mileageOrHours());
        fuelLog.setNotes(normalize(request.notes()));
        return toResponse(fuelLogRepository.save(fuelLog));
    }

    @Transactional(readOnly = true)
    public List<FuelLogResponse> getFuelLogs(Long equipmentId) {
        getEquipment(equipmentId);
        return fuelLogRepository.findByEquipmentIdOrderByDateDesc(equipmentId).stream()
                .map(this::toResponse)
                .toList();
    }

    private Equipment getEquipment(Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private FuelLogResponse toResponse(FuelLog fuelLog) {
        return new FuelLogResponse(
                fuelLog.getId(),
                fuelLog.getEquipment().getId(),
                fuelLog.getDate(),
                fuelLog.getLiters(),
                fuelLog.getCostPerLiter(),
                fuelLog.getTotalCost(),
                fuelLog.getMileageOrHours(),
                fuelLog.getNotes(),
                fuelLog.getCreatedAt());
    }
}

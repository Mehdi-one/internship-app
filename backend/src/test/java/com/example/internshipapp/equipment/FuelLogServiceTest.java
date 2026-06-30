package com.example.internshipapp.equipment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.internshipapp.common.exception.ResourceNotFoundException;
import com.example.internshipapp.equipment.dto.FuelLogRequest;

class FuelLogServiceTest {

    private EquipmentRepository equipmentRepository;
    private FuelLogRepository fuelLogRepository;
    private FuelLogService service;

    @BeforeEach
    void setUp() {
        equipmentRepository = mock(EquipmentRepository.class);
        fuelLogRepository = mock(FuelLogRepository.class);
        service = new FuelLogService(equipmentRepository, fuelLogRepository);
    }

    @Test
    void computesTotalCostFromLitersAndCostPerLiter() {
        Equipment equipment = new Equipment();
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(fuelLogRepository.save(any(FuelLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.addFuelLog(1L, new FuelLogRequest(
                LocalDate.of(2026, 6, 30),
                new BigDecimal("40.50"),
                new BigDecimal("13.25"),
                new BigDecimal("1200"),
                "Plein chantier"));

        assertThat(response.totalCost()).isEqualByComparingTo("536.63");
    }

    @Test
    void returnsFuelLogsInRepositoryDescendingOrder() {
        Equipment equipment = new Equipment();
        FuelLog newest = fuelLog(equipment, LocalDate.of(2026, 6, 30));
        FuelLog oldest = fuelLog(equipment, LocalDate.of(2026, 6, 10));
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(fuelLogRepository.findByEquipmentIdOrderByDateDesc(1L)).thenReturn(List.of(newest, oldest));

        var response = service.getFuelLogs(1L);

        assertThat(response).extracting(item -> item.date()).containsExactly(newest.getDate(), oldest.getDate());
    }

    @Test
    void throwsNotFoundWhenEquipmentDoesNotExist() {
        when(equipmentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addFuelLog(99L, new FuelLogRequest(
                LocalDate.now(), BigDecimal.ONE, BigDecimal.TEN, null, null)))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Equipment not found");
    }

    private FuelLog fuelLog(Equipment equipment, LocalDate date) {
        FuelLog fuelLog = new FuelLog();
        fuelLog.setEquipment(equipment);
        fuelLog.setDate(date);
        fuelLog.setLiters(BigDecimal.TEN);
        fuelLog.setCostPerLiter(BigDecimal.TEN);
        fuelLog.setTotalCost(new BigDecimal("100.00"));
        return fuelLog;
    }
}

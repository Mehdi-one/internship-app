package com.example.internshipapp.equipment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.internshipapp.common.enums.EquipmentStatus;
import com.example.internshipapp.common.enums.EquipmentType;
import com.example.internshipapp.common.enums.UsageCostType;

class EquipmentServiceTest {

    private EquipmentRepository equipmentRepository;
    private EquipmentService service;

    @BeforeEach
    void setUp() {
        equipmentRepository = mock(EquipmentRepository.class);
        service = new EquipmentService(equipmentRepository);
    }

    @Test
    void maintenanceDueSoonIsTrueAtFifteenDays() {
        assertThat(responseWithDates(LocalDate.now().plusDays(15), null).maintenanceDueSoon()).isTrue();
    }

    @Test
    void maintenanceDueSoonIsFalseAtFortyFiveDays() {
        assertThat(responseWithDates(LocalDate.now().plusDays(45), null).maintenanceDueSoon()).isFalse();
    }

    @Test
    void maintenanceDueSoonIsFalseWithoutDate() {
        assertThat(responseWithDates(null, null).maintenanceDueSoon()).isFalse();
    }

    @Test
    void insuranceExpiringSoonIsTrueAtFifteenDays() {
        assertThat(responseWithDates(null, LocalDate.now().plusDays(15)).insuranceExpiringSoon()).isTrue();
    }

    @Test
    void insuranceExpiringSoonIsFalseAtFortyFiveDays() {
        assertThat(responseWithDates(null, LocalDate.now().plusDays(45)).insuranceExpiringSoon()).isFalse();
    }

    @Test
    void insuranceExpiringSoonIsFalseWithoutDate() {
        assertThat(responseWithDates(null, null).insuranceExpiringSoon()).isFalse();
    }

    private com.example.internshipapp.equipment.dto.EquipmentResponse responseWithDates(
            LocalDate maintenanceDate,
            LocalDate insuranceDate) {
        Equipment equipment = new Equipment();
        equipment.setReference("EQ-001");
        equipment.setType(EquipmentType.TRUCK);
        equipment.setBrandModel("Test");
        equipment.setUsageCostType(UsageCostType.HOURLY);
        equipment.setUsageCost(BigDecimal.TEN);
        equipment.setStatus(EquipmentStatus.AVAILABLE);
        equipment.setNextMaintenanceDate(maintenanceDate);
        equipment.setInsuranceExpiryDate(insuranceDate);
        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        return service.findById(1L);
    }
}

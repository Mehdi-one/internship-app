package com.example.internshipapp.equipment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelLogRepository extends JpaRepository<FuelLog, Long> {

    List<FuelLog> findByEquipmentIdOrderByDateDesc(Long equipmentId);
}

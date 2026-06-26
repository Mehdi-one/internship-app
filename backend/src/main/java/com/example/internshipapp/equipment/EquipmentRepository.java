package com.example.internshipapp.equipment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.internshipapp.common.enums.EquipmentStatus;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    boolean existsByReference(String reference);

    Optional<Equipment> findByReference(String reference);

    List<Equipment> findAllByOrderByUpdatedAtDesc();

    List<Equipment> findByStatusOrderByUpdatedAtDesc(EquipmentStatus status);

    @Query("""
            select e from Equipment e
            where lower(e.reference) like lower(concat('%', :search, '%'))
               or lower(e.brandModel) like lower(concat('%', :search, '%'))
            order by e.updatedAt desc
            """)
    List<Equipment> search(@Param("search") String search);

    @Query("""
            select e from Equipment e
            where e.status = :status
              and (lower(e.reference) like lower(concat('%', :search, '%'))
                   or lower(e.brandModel) like lower(concat('%', :search, '%')))
            order by e.updatedAt desc
            """)
    List<Equipment> searchByStatus(@Param("search") String search, @Param("status") EquipmentStatus status);
}

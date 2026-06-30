package com.example.internshipapp.employee;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.internshipapp.common.enums.ContractType;
import com.example.internshipapp.common.enums.EmployeeStatus;
import com.example.internshipapp.employee.dto.EmployeeRequest;

class EmployeeServiceTest {

    private EmployeeRepository employeeRepository;
    private EmployeeCostHistoryRepository costHistoryRepository;
    private EmployeeService service;

    @BeforeEach
    void setUp() {
        employeeRepository = mock(EmployeeRepository.class);
        costHistoryRepository = mock(EmployeeCostHistoryRepository.class);
        service = new EmployeeService(employeeRepository, costHistoryRepository);
    }

    @Test
    void createsInitialCostHistoryWithEmployee() {
        when(employeeRepository.existsByMatricule("EMP-001")).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.create(request("EMP-001", "120.00"));

        ArgumentCaptor<EmployeeCostHistory> captor = ArgumentCaptor.forClass(EmployeeCostHistory.class);
        verify(costHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getHourlyCost()).isEqualByComparingTo("120.00");
        assertThat(captor.getValue().getEffectiveDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void createsHistoryWhenHourlyCostChanges() {
        Employee employee = new Employee();
        employee.setMatricule("EMP-001");
        employee.setFullName("Ahmed Test");
        employee.setQualification("Macon");
        employee.setContractType(ContractType.CDI);
        employee.setHourlyCost(new BigDecimal("100.00"));
        employee.setStatus(EmployeeStatus.ACTIVE);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.findByMatricule("EMP-001")).thenReturn(Optional.empty());
        when(employeeRepository.save(employee)).thenReturn(employee);

        service.update(1L, request("EMP-001", "130.00"));

        ArgumentCaptor<EmployeeCostHistory> captor = ArgumentCaptor.forClass(EmployeeCostHistory.class);
        verify(costHistoryRepository).save(captor.capture());
        assertThat(captor.getValue().getHourlyCost()).isEqualByComparingTo("130.00");
    }

    @Test
    void resolvesLatestCostEffectiveOnPointageDate() {
        Employee employee = mock(Employee.class);
        EmployeeCostHistory history = new EmployeeCostHistory();
        history.setEmployee(employee);
        history.setHourlyCost(new BigDecimal("140.00"));
        history.setEffectiveDate(LocalDate.of(2026, 6, 1));
        LocalDate pointageDate = LocalDate.of(2026, 6, 15);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(costHistoryRepository.findTopByEmployeeIdAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(
                1L, pointageDate)).thenReturn(Optional.of(history));

        assertThat(service.getCostAtDate(1L, pointageDate)).isEqualByComparingTo("140.00");
    }

    private EmployeeRequest request(String matricule, String hourlyCost) {
        return new EmployeeRequest(
                matricule,
                "Ahmed Test",
                "Macon",
                ContractType.CDI,
                new BigDecimal(hourlyCost),
                EmployeeStatus.ACTIVE);
    }
}

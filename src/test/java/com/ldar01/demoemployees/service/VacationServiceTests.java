package com.ldar01.demoemployees.service;

import com.ldar01.demoemployees.dto.request.vacation.VacationRequest;
import com.ldar01.demoemployees.dto.request.vacation.VacationUpdateRequest;
import com.ldar01.demoemployees.dto.response.vacation.VacationResponse;
import com.ldar01.demoemployees.entities.Department;
import com.ldar01.demoemployees.entities.Employee;
import com.ldar01.demoemployees.entities.Vacation;
import com.ldar01.demoemployees.entities.VacationStatus;
import com.ldar01.demoemployees.enums.VacationStatusState;
import com.ldar01.demoemployees.exception.BusinessRuleViolationException;
import com.ldar01.demoemployees.exception.EmployeeNotFoundException;
import com.ldar01.demoemployees.exception.VacationNotFoundException;
import com.ldar01.demoemployees.repository.EmployeeRepository;
import com.ldar01.demoemployees.repository.VacationRepository;
import com.ldar01.demoemployees.repository.VacationStatusRepository;
import com.ldar01.demoemployees.service.impl.VacationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/*

   Merely for learning purposes :P

 */

@ExtendWith(MockitoExtension.class)
public class VacationServiceTests {

    private Vacation vacationEntityMockResponse;
    private VacationRequest newVacationMockRequest;
    private VacationUpdateRequest existingVacationMockRequest;
    private VacationStatus pendingStatus;
    private VacationStatus approvedStatus;
    private Employee employee;
    private final int dummyId = 1;

    @Mock
    private VacationRepository vacationRepository;
    @Mock
    private VacationStatusRepository vacationStatusRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @InjectMocks
    private VacationServiceImpl vacationServiceImpl;

    @BeforeEach
    public void setup() {
        pendingStatus = VacationStatus
                .builder()
                .id(1)
                .status(VacationStatusState.PENDING.getState())
                .build();

        approvedStatus = VacationStatus
                .builder()
                .id(2)
                .status(VacationStatusState.APPROVED.getState())
                .build();

        Department department = Department.builder().id(1).departmentName("SomeDepartment").build();

        employee = Employee.builder().id(1).name("John").lastName("Doe").department(department).build();

        vacationEntityMockResponse = Vacation
                .builder()
                .id(dummyId)
                .startDate(LocalDate.of(2025, 3, 3))
                .endDate(LocalDate.of(2025, 3, 10))
                .reason("This is some extremely valid reason LOL")
                .employee(employee)
                .status(pendingStatus)
                .build();

        newVacationMockRequest = new VacationRequest(
                LocalDate.of(2025, 3, 3),
                LocalDate.of(2025, 3, 10),
                "This is some extremely valid reason LOL",
                employee.getId()
        );

        existingVacationMockRequest = new VacationUpdateRequest(
                LocalDate.of(2025, 3, 3),
                LocalDate.of(2025, 3, 10),
                "This is some extremely valid reason LOL",
                VacationStatusState.APPROVED.getState(),
                employee.getId()
        );

        List<VacationStatus> cache = List.of(pendingStatus, approvedStatus);

        when(vacationStatusRepository.findAll()).thenReturn(cache);

        vacationServiceImpl.initializeCache();
    }


    @Test
    public void save_givenValidInput_shouldReturnSavedVacation() {
        //Setup return values

        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.ofNullable(employee));
        when(vacationRepository.save(Mockito.any(Vacation.class))).thenReturn(vacationEntityMockResponse);

        //Assertions
        VacationResponse response = vacationServiceImpl.save(newVacationMockRequest);
        Assertions.assertNotNull(response);
    }

    @Test
    public void save_givenInvalidDates_shouldThrowBusinessRuleViolationException() {
        //Setup return values
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.ofNullable(employee));

        //Setup mock request with an invalid start date
        VacationRequest invalidRequest = new VacationRequest(
                LocalDate.of(2025, 4, 15),
                LocalDate.of(2025, 3, 10),
                "This is some extremely valid reason LOL",
                dummyId
        );

        //Assertions
        Assertions.assertThrows(BusinessRuleViolationException.class,
                () -> vacationServiceImpl.save(invalidRequest));
    }

    @Test
    public void save_givenInvalidEmployeeId_shouldThrowEmployeeNotFoundException() {
        //Setup return values
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.empty());

        //Assertions
        Assertions.assertThrows(EmployeeNotFoundException.class,
                () -> vacationServiceImpl.save(newVacationMockRequest));
    }

    @Test
    public void update_givenInvalidVacationId_shouldThrowVacationNotFoundException() {
        when(vacationRepository.findById(dummyId))
                .thenReturn(Optional.empty());

        //Assertions
        Assertions.assertThrows(VacationNotFoundException.class,
                () -> vacationServiceImpl.update(dummyId, existingVacationMockRequest));
    }

    @Test
    public void update_givenInvalidDates_shouldThrowBusinessRuleViolationException() {
        //Setup return values
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.ofNullable(employee));
        when(vacationRepository.findById(dummyId))
                .thenReturn(Optional.ofNullable(vacationEntityMockResponse));

        VacationUpdateRequest invalidRequest = new VacationUpdateRequest(
                LocalDate.of(2025, 4, 3),
                LocalDate.of(2025, 3, 10),
                "This is some extremely valid reason LOL",
                VacationStatusState.PENDING.getState(),
                employee.getId()
        );

        //Assertions
        Assertions.assertThrows(BusinessRuleViolationException.class,
                () -> vacationServiceImpl.update(dummyId, invalidRequest));
    }

    @Test
    public void update_givenValidStatus_shouldUpdateVacationStatus() {
        VacationUpdateRequest updateMockRequest = new VacationUpdateRequest(
                LocalDate.of(2025, 3, 3),
                LocalDate.of(2025, 3, 10),
                "This is some extremely valid reason LOL",
                VacationStatusState.APPROVED.getState(),
                employee.getId()
        );

        vacationEntityMockResponse.setStatus(approvedStatus);

        //Setup return values
        when(vacationRepository.findById(dummyId)).thenReturn(Optional.ofNullable(vacationEntityMockResponse));
        when(vacationStatusRepository.findByStatus(Mockito.anyString())).thenReturn(Optional.ofNullable(approvedStatus));
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.ofNullable(employee));
        when(vacationRepository.save(Mockito.any(Vacation.class))).thenReturn(vacationEntityMockResponse);

        VacationResponse result = vacationServiceImpl.update(dummyId, updateMockRequest);

        //Assertions
        Assertions.assertNotNull(result);
        //Assert that the vacation was approved
        Assertions.assertEquals(result.status(), VacationStatusState.APPROVED.getState());
    }
}

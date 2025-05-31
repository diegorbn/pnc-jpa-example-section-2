package com.ldar01.demoemployees.service.impl;

import com.ldar01.demoemployees.dto.request.vacation.VacationRequest;
import com.ldar01.demoemployees.dto.request.vacation.VacationUpdateRequest;
import com.ldar01.demoemployees.dto.response.vacation.VacationResponse;
import com.ldar01.demoemployees.entities.Employee;
import com.ldar01.demoemployees.entities.Vacation;
import com.ldar01.demoemployees.entities.VacationStatus;
import com.ldar01.demoemployees.exception.*;
import com.ldar01.demoemployees.repository.EmployeeRepository;
import com.ldar01.demoemployees.repository.VacationRepository;
import com.ldar01.demoemployees.repository.VacationStatusRepository;
import com.ldar01.demoemployees.service.VacationService;
import com.ldar01.demoemployees.utils.mappers.VacationMapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

import com.ldar01.demoemployees.enums.VacationStatusState;

@Service
@AllArgsConstructor
public class VacationServiceImpl implements VacationService {
    private final VacationRepository vacationRepository;
    private final VacationStatusRepository vacationStatusRepository;
    private final EmployeeRepository employeeRepository;
    private final Map<String, VacationStatus> statusCache = new HashMap<>();

    @PostConstruct
    public void initializeCache() {
        vacationStatusRepository.findAll().forEach(status -> statusCache.put(status.getStatus(), status));
    }

    @Override
    public List<VacationResponse> findAllVacations() {
        return vacationRepository.findAll().stream()
                                 .map(VacationMapper::toDTO)
                                 .toList();
    }

    @Override
    public VacationResponse findVacationById(int id) {
        Vacation vacation = vacationRepository.findById(id)
                                              .orElseThrow(() -> new VacationNotFoundException("No vacation request " +
                                                      "was found with the given ID"));
        return VacationMapper.toDTO(vacation);
    }

    @Override
    public VacationResponse save(VacationRequest vacation) {
        //Critical error
        Employee employee = employeeRepository.findById(vacation.employeeId())
                                              .orElseThrow(() -> new EmployeeNotFoundException("No such vacation " +
                                                      "status found"));

        List<String> errors = new ArrayList<>();

        if (vacation.startDate().isAfter(vacation.endDate())) {
            errors.add("The start date must be before the end date");
        }
        //Retrieve the status from the cache to avoid database calls
        VacationStatus status = statusCache.get(VacationStatusState.PENDING.getState());
        //Retrieve the employee entity from the database

        if (!errors.isEmpty()) {
            throw new BusinessRuleViolationException("The vacation request cannot be created due to the following " +
                    "errors", errors);
        }

        return VacationMapper.toDTO(vacationRepository.save(VacationMapper.toEntity(vacation, status, employee)));
    }

    @Override
    public VacationResponse update(int id, VacationUpdateRequest vacation) {
        //Critical errors
        Vacation persistedVacation =
                vacationRepository.findById(id).orElseThrow(() -> new VacationNotFoundException("No " +
                        "vacation request was found with the given ID"));

        Employee employee =
                employeeRepository.findById(vacation.employeeId()).orElseThrow(() -> new EmployeeNotFoundException(
                        "No employee was found with the given ID"));

        //Business rules validation
        List<String> errors = new ArrayList<>();
        if (vacation.startDate().isAfter(vacation.endDate())) {
            errors.add("The start date must be before the end date");
        }
        //Could use cache here but I'm too lazy to refactor :P
        VacationStatus newStatus = vacationStatusRepository.findByStatus(vacation.status()).orElse(null);

        if (newStatus == null) {
            errors.add(vacation.status() + " is not a valid vacation status");
        }

        if (!errors.isEmpty()) {
            throw new BusinessRuleViolationException("The vacation request cannot be created due to the following " +
                    "errors", errors);
        }
        //Update the persisted vacation request with the new values provided
        Vacation updatedVacation = VacationMapper.toEntityUpdate(persistedVacation, newStatus, employee, vacation);
        return VacationMapper.toDTO(
                vacationRepository.save(updatedVacation)
        );
    }

    @Override
    public List<VacationResponse> findAllByEmployeeId(int employeeId) {
        //Check if the employee exists in the database
        boolean isValid = employeeRepository.findById(employeeId).isPresent();
        if (!isValid) {
            throw new EmployeeNotFoundException("No employee was found with the given ID");
        }
        //Retrieve all vacations for the given employee
        List<Vacation> vacations = vacationRepository.findAllByEmployeeId(employeeId);
        return vacations.stream()
                        .map(VacationMapper::toDTO)
                        .toList();
    }

    @Override
    public void delete(int id) {
        vacationRepository.findById(id).orElseThrow(() -> new VacationNotFoundException("No vacation was found with " +
                "the given ID"));
        vacationRepository.deleteById(id);
    }
}

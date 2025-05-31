package com.ldar01.demoemployees.service;

import com.ldar01.demoemployees.dto.request.vacation.VacationRequest;
import com.ldar01.demoemployees.dto.request.vacation.VacationUpdateRequest;
import com.ldar01.demoemployees.dto.response.vacation.VacationResponse;

import java.util.List;

public interface VacationService {
    List<VacationResponse> findAllVacations();
    List<VacationResponse> findAllByEmployeeId(int employeeId);
    VacationResponse findVacationById(int id);
    VacationResponse save(VacationRequest vacation);
    VacationResponse update(int id, VacationUpdateRequest vacation);
    void delete(int id);
}

package com.ldar01.demoemployees.utils.mappers;

import com.ldar01.demoemployees.dto.request.vacation.VacationRequest;
import com.ldar01.demoemployees.dto.request.vacation.VacationUpdateRequest;
import com.ldar01.demoemployees.dto.response.vacation.VacationResponse;
import com.ldar01.demoemployees.entities.Employee;
import com.ldar01.demoemployees.entities.Vacation;
import com.ldar01.demoemployees.entities.VacationStatus;

public class VacationMapper {
    public static Vacation toEntity(VacationRequest vacationDTO, VacationStatus status, Employee employee) {
        return Vacation
                .builder()
                .startDate(vacationDTO.startDate())
                .endDate(vacationDTO.endDate())
                .reason(vacationDTO.reason())
                .status(status)
                .employee(employee)
                .build();
    }

    public static VacationResponse toDTO(Vacation vacation) {
        return new VacationResponse(
                vacation.getId(),
                vacation.getStartDate(),
                vacation.getEndDate(),
                vacation.getReason(),
                vacation.getEmployee().getName(),
                vacation.getStatus().getStatus()
        );
    }

    public static Vacation toEntityUpdate(
            Vacation vacation, VacationStatus status, Employee employee,
            VacationUpdateRequest request
    ) {
        vacation.setStartDate(request.startDate());
        vacation.setEndDate(request.endDate());
        vacation.setReason(request.reason());
        vacation.setStatus(status);
        vacation.setEmployee(employee);
        return vacation;
    }
}

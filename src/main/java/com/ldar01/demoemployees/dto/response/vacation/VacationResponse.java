package com.ldar01.demoemployees.dto.response.vacation;

import java.time.LocalDate;

public record VacationResponse(
        Integer id,
        LocalDate startDate,
        LocalDate endDate,
        String reason,
        String EmployeeName,
        String status
) {
}

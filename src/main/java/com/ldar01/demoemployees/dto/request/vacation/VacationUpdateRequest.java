package com.ldar01.demoemployees.dto.request.vacation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record VacationUpdateRequest(
        @NotNull(message = "You must enter a start date")
        LocalDate startDate,
        @NotNull(message = "You must enter an end date")
        LocalDate endDate,
        @NotBlank(message = "A reason needs to be provided")
        String reason,
        @NotBlank(message = "Your must enter a valid vacation status")
        String status,
        @NotNull(message = "You must provide a valid employee ID")
        Integer employeeId
) {
}

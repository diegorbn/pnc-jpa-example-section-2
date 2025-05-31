package com.ldar01.demoemployees.controller;

import com.ldar01.demoemployees.dto.request.vacation.VacationRequest;
import com.ldar01.demoemployees.dto.request.vacation.VacationUpdateRequest;
import com.ldar01.demoemployees.dto.response.GeneralResponse;
import com.ldar01.demoemployees.dto.response.vacation.VacationResponse;
import com.ldar01.demoemployees.service.VacationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/vacation-requests")
@AllArgsConstructor
public class VacationController {
    private final VacationService vacationService;

    @GetMapping()
    public ResponseEntity<GeneralResponse> getAllVacationRequests() {
        List<VacationResponse> vacationRequests = vacationService.findAllVacations();
        return buildResponse("Vacation requests found", HttpStatus.OK, vacationRequests);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralResponse> getVacationById(@PathVariable int id) {
        VacationResponse vacationResponse = vacationService.findVacationById(id);
        return buildResponse("Vacation request found", HttpStatus.OK, vacationResponse);
    }

    @PostMapping()
    public ResponseEntity<GeneralResponse> saveVacation(@RequestBody @Valid VacationRequest vacation) {
        VacationResponse savedVacation = vacationService.save(vacation);
        return buildResponse("Vacation request created", HttpStatus.CREATED, savedVacation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeneralResponse> updateVacation(
            @PathVariable int id,
            @RequestBody @Valid VacationUpdateRequest vacation
    ) {
        VacationResponse vacationResponse = vacationService.update(id, vacation);
        return buildResponse("Vacation request updated", HttpStatus.OK, vacationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GeneralResponse> deleteVacation(@PathVariable int id) {
        vacationService.delete(id);
        return buildResponse("Vacation request deleted", HttpStatus.NO_CONTENT, null);
    }

    public ResponseEntity<GeneralResponse> buildResponse(String message, HttpStatus status, Object data) {
        String uri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().getPath();
        return ResponseEntity.status(status).body(GeneralResponse
                .builder()
                .message(message)
                .status(status.value())
                .data(data)
                .uri(uri)
                .time(LocalDate.now())
                .build()
        );
    }
}

package com.ldar01.demoemployees.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Vacation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "reason")
    private String reason;
    @ManyToOne
    @JoinColumn(name = "status_id")
    private VacationStatus status;
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;
}


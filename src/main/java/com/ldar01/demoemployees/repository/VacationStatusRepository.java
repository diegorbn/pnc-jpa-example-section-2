package com.ldar01.demoemployees.repository;

import com.ldar01.demoemployees.entities.VacationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VacationStatusRepository extends JpaRepository<VacationStatus, Integer> {
    Optional<VacationStatus> findByStatus(String status);
}

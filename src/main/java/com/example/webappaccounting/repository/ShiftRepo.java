package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Shift;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ShiftRepo extends CrudRepository<Shift, Long> {
    Optional<Shift> findAllByShiftDate(LocalDateTime shiftDate);

    Optional<Shift> findAllByShiftDateAndDescriptionAndName(LocalDateTime shiftDate, String description, String name);

    Optional<Shift> findAllByShiftDateAndDescriptionAndNameAndShiftType(
            LocalDateTime shiftDate,
            String description,
            String name,
            String shiftType);
}

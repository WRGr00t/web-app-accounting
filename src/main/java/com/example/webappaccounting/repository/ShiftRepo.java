package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Shift;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepo extends CrudRepository<Shift, Long> {
    Optional<Shift> findAllByShiftDate(LocalDateTime shiftDate);

    Optional<Shift> findAllByShiftDateAndDescriptionAndName(
            LocalDateTime shiftDate,
            String description,
            String name);

    Optional<Shift> findAllByShiftDateAndDescriptionAndNameAndShiftType(
            LocalDateTime shiftDate,
            String description,
            String name,
            String shiftType);

    long countByNameAndShiftDateBetween(
            String name,
            LocalDateTime dayStart,

            LocalDateTime dayEnd);

    long countByDescriptionAndShiftDateBetween(
            String description,
            LocalDateTime dayStart,
            LocalDateTime dayEnd);

    List<Shift> findAllByNameAndShiftDateBetween(
            String name,
            LocalDateTime dayStart,
            LocalDateTime dayEnd);
}

package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Shift;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ShiftRepo extends CrudRepository<Shift, Long> {
    List<Shift> findAllByShiftDate(LocalDate shiftDate);

    Shift findOneByNameAndShiftDate(String name,
                                          LocalDate shiftDate);

    Optional<Shift> findAllByShiftDateAndName(
            LocalDate shiftDate,
            String name);

    Optional<Shift> findAllByNameAndShiftDateAndDescription(
            String name,
            LocalDate shiftDate,
            String description);

    Optional<Shift> findAllByShiftDateAndDescriptionAndNameAndShiftType(
            LocalDate shiftDate,
            String description,
            String name,
            String shiftType);

    long countByNameAndShiftDateBetween(
            String name,
            LocalDate dayStart,

            LocalDate dayEnd);

    long countByDescriptionAndShiftDateBetween(
            String description,
            LocalDate dayStart,
            LocalDate dayEnd);

    List<Shift> findAllByNameAndShiftDateBetween(
            String name,
            LocalDate dayStart,
            LocalDate dayEnd);

    List<Shift> findAllByShiftDateBetween(
            LocalDate dayStart,
            LocalDate dayEnd);

    @Query(value = "Select min(shift_date) from shifts", nativeQuery = true)
    LocalDate findMinimum();

    @Query(value = "Select max(shift_date) from shifts", nativeQuery = true)
    LocalDate findMaximum();

}

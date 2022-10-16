package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Employee;
import com.example.webappaccounting.model.Shift;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface EmployeeRepo extends CrudRepository<Employee, Long> {
    Optional<Employee> findAllByNameAndShiftType(String name, String shiftType);
}

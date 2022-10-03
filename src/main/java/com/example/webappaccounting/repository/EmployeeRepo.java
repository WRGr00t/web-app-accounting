package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Employee;
import org.springframework.data.repository.CrudRepository;

public interface EmployeeRepo extends CrudRepository<Employee, Long> {
}

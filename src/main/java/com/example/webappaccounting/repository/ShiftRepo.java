package com.example.webappaccounting.repository;

import com.example.webappaccounting.model.Shift;
import org.springframework.data.repository.CrudRepository;

public interface ShiftRepo extends CrudRepository<Shift, Long> {
}

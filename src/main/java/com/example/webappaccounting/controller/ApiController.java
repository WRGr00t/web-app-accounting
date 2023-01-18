package com.example.webappaccounting.controller;

import com.example.webappaccounting.exceptions.ShiftNotFoundException;
import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    ShiftRepo shiftRepo;

    @GetMapping
    public List<Shift> list() {
        return (List<Shift>) shiftRepo.findAll();
    }

    @GetMapping("{id}")
    public Shift getOne(@PathVariable Long id) {
        return getShift(id);
    }

    private Shift getShift(Long id) {
        return shiftRepo.findById(id)
                .orElseThrow(() -> new ShiftNotFoundException(id));
    }

    @PostMapping
    public Shift create (@RequestBody Shift shift) {
        return shiftRepo.save(shift);
    }

    @PutMapping("{id}")
    public Shift updateShift(@PathVariable Long id, @RequestBody Shift shift) {
        return shiftRepo.findById(id)
                .map(s -> {
                    s.setShiftDate(shift.getShiftDate());
                    s.setDescription(shift.getDescription());
                    s.setName(shift.getName());
                    s.setShiftType(shift.getShiftType());
                    return shiftRepo.save(s);
                })
                .orElseGet(() -> {
                    shift.setId(id);
                    return shiftRepo.save(shift);
                });
    }

    @DeleteMapping("{id}")
    public void deleteShift(@PathVariable Long id) {
        shiftRepo.deleteById(id);
    }

}

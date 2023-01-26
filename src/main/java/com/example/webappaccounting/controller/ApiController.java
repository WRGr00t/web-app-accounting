package com.example.webappaccounting.controller;

import com.example.webappaccounting.exceptions.ShiftNotFoundException;
import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    ShiftRepo shiftRepo;
    @Autowired
    ShiftServiceImpl service;

    @GetMapping
    public String list() {
        ArrayList<Shift> shiftIterable = (ArrayList<Shift>) shiftRepo.findAllByShiftDate(LocalDate.now());

        ParseHelper helper = new ParseHelper(shiftRepo, service);
        StringBuilder dayShift = new StringBuilder();
        StringBuilder nigthShift = new StringBuilder();
        for (Shift s : shiftIterable) {
            if (helper.isShiftTime(s.getDescription()) && !s.getShiftType().equals("8*5")) {
                if (helper.isNightShift(s)) {
                    nigthShift.append(s.getName())
                            .append("\n");
                } else {
                    dayShift.append(s.getName())
                            .append("\n");
                }
            }
        }
        dayShift
                .append("В ночь:\n")
                .append(nigthShift);
        return dayShift.toString().trim();
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

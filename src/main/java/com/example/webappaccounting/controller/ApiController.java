package com.example.webappaccounting.controller;

import com.example.webappaccounting.exceptions.ShiftNotFoundException;
import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.response.CalendarResponse;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api")
public class ApiController {
    @Autowired
    ShiftRepo shiftRepo;
    @Autowired
    ShiftServiceImpl service;
    @Autowired
    ParseHelper parseHelper;

    @GetMapping
    public String list(@RequestParam(required = false) String requestDate) {

        LocalDate date;
        if (requestDate == null) {
            date = LocalDate.now();
        } else date = LocalDate.parse(requestDate);
        ArrayList<Shift> shiftIterable = (ArrayList<Shift>) shiftRepo.findAllByShiftDate(date);

        StringBuilder dayShift = new StringBuilder();
        StringBuilder nightShift = new StringBuilder();
        for (Shift s : shiftIterable) {
            if (parseHelper.isShiftTime(s.getDescription()) && !s.getShiftType().equals("8*5")) {
                if (parseHelper.isNightShift(s)) {
                    nightShift.append(s.getName())
                            .append("\n");
                } else {
                    dayShift.append(s.getName())
                            .append("\n");
                }
            }
        }
        dayShift
                .append("В ночь:\n")
                .append(nightShift);
        return dayShift.toString().trim();
    }

    @GetMapping("{id}")
    public Shift getOne(@PathVariable Long id) {
        return getShift(id);
    }

    @GetMapping("/byname")
    public ArrayList<LocalDate> getByName(@RequestParam String name, @RequestParam int year) {
        return getShiftByName(name, year);
    }

    @GetMapping("/isnight")
    public Boolean getByName(@RequestParam String name, @RequestParam String date) {
        boolean result;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        formatter = formatter.withLocale(Locale.forLanguageTag("ru-RU"));
        LocalDate localDate = LocalDate.parse(date, formatter);
        Shift shift = shiftRepo.findOneByNameAndShiftDate(name, localDate);
        if (shift != null) {
            result = parseHelper.isNightShift(shift);
        } else {
            result = false;
        }

        return result;
    }

    @GetMapping("/bynameandmonth")
    public ArrayList<CalendarResponse> getShiftForCalendar(@RequestParam String name,
                                                           @RequestParam int year,
                                                           @RequestParam(required = false) String monthNumber) {
        ArrayList<Shift> list;
        if (monthNumber != null && !monthNumber.trim().isEmpty()) {
            list = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(
                    name,
                    LocalDate.of(year, Integer.parseInt(monthNumber), 1),
                    LocalDate.of(year, Integer.parseInt(monthNumber),
                            LocalDate.of(year, Integer.parseInt(monthNumber), 1).lengthOfMonth()));
        } else {
            list = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(
                    name,
                    LocalDate.of(year, 1, 1),
                    LocalDate.of(year, 12, 31));
        }

        return (ArrayList<CalendarResponse>) list.stream()
                .map(x -> new CalendarResponse(
                        x.getName(),
                        x.getShiftDate(),
                        parseHelper.getStatus(x),
                        x.getDescription()))
                .collect(Collectors.toList());
    }

    private Shift getShift(Long id) {
        return shiftRepo.findById(id)
                .orElseThrow(() -> new ShiftNotFoundException(id));
    }

    private ArrayList<LocalDate> getShiftByName(String name, int year) {
        ArrayList<Shift> list = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(
                name,
                LocalDate.of(year, 1, 1),
                LocalDate.of(year, 12, 31));
        List<LocalDate> result = list.stream()
                .filter(shift -> Pattern.matches("^\\d{1,2}\\-\\d{1,2}$", shift.getDescription()))
                .map(Shift::getShiftDate)
                .collect(Collectors.toList());
        return (ArrayList<LocalDate>) result;
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

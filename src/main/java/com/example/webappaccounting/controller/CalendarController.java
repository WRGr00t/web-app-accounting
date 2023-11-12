package com.example.webappaccounting.controller;


import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class CalendarController {

    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ShiftServiceImpl service;

    @Autowired
    private ParseHelper parseHelper;

    @GetMapping("calendar")
    public String GetCalendar(@RequestParam(name="person", required=false) String person,
                              Map<String, Object> model){

        if (person == null) {
            person = "";
        }
        model.put("select", person);

        LocalDate startDay = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDay = LocalDate.of(LocalDate.now().getYear(), 12, 31);

        HashSet<String> persons = (HashSet<String>) parseHelper.getNameInRangeWithout85(startDay, endDay);

        ArrayList<String> names = (ArrayList<String>) persons.stream()
                .sorted()
                .collect(Collectors.toList());

        model.put("persons", names);
        return "calendar";
    }

    @GetMapping("monthcalendar")
    public String GetMonthCalendar(@RequestParam(name="person", required=false) String person,
                              Map<String, Object> model){

        if (person == null) {
            person = "";
        }
        model.put("select", person);

        LocalDate startDay = LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDay = LocalDate.of(LocalDate.now().getYear(), 12, 31);
        HashSet<String> persons = (HashSet<String>) parseHelper.getNameInRangeWithout85(startDay, endDay);

        ArrayList<String> names = (ArrayList<String>) persons.stream()
                .sorted()
                .collect(Collectors.toList());

        model.put("persons", names);
        return "monthcalendar";
    }
}

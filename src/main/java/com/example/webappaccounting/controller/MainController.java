package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Controller
public class MainController {
    @Autowired
    private ShiftRepo shiftRepo;

    @Value("${upload.path}")
    private String UPLOAD_DIR;

    @GetMapping("/")
    public String home(Map<String, Object> model){
        LocalDate day = LocalDate.now();
        model.put("today", day);
        return "home";
    }

    @GetMapping("/inshift")
    public String main(@RequestParam(name="calendar", required=false) String date,
                       Map<String, Object> model) {
        Iterable<Shift> shiftIterable = shiftRepo.findAll();
        ArrayList<Shift> list = new ArrayList<>();
        ArrayList<Shift> nightShift = new ArrayList<>();
        ParseHelper helper = new ParseHelper(shiftRepo);

        LocalDateTime requestDate = helper.StringToLocalDateTime(date);

        for (Shift s : shiftIterable) {
            if (s.getShiftDate().isAfter(requestDate.minusMinutes(1)) &&
                    s.getShiftDate().isBefore(requestDate.plusMinutes(1)) &&
                    helper.isShiftTime(s.getDescription())) {
                if (helper.isNightShift(s)) {
                    nightShift.add(s);
                } else {
                    list.add(s);
                }
            }
        }
        LocalDate localDate = requestDate.toLocalDate();
        LocalDate startYear = LocalDate.of(LocalDate.now().getYear(),1, 1);
        LocalDate endYear = LocalDate.of(LocalDate.now().getYear(),12, 31);
        LocalDate today = LocalDate.now();
        model.put("startYear", startYear);
        model.put("endYear", endYear);
        model.put("today", today);
        model.put("date", localDate);
        model.put("repos", list);
        model.put("nights", nightShift);

        return "inshift";
    }

    @GetMapping("/inmonth")
    public String month(@RequestParam(name="start", required=false) String start,
                        @RequestParam(name="end", required=false) String end,
                       Map<String, Object> model) {

        if (start == null || end == null) {
            start = LocalDate.now().toString();
            end = LocalDate.now().toString();
        }
        ParseHelper helper = new ParseHelper(shiftRepo);
        LocalDate startYear = LocalDate.of(LocalDate.now().getYear(),1, 1);
        LocalDate endYear = LocalDate.of(LocalDate.now().getYear(),12, 31);
        LocalDate startDay = helper.getDateFromString(start);
        LocalDate endDay = helper.getDateFromString(end);
        //LocalDateTime startRange = startDay.atStartOfDay();
        //LocalDateTime endRange = endDay.atTime(23,59,59);

        model.put("startYear", startYear);
        model.put("endYear", endYear);

        model.put("dateStart", startDay);
        model.put("dateEnd", endDay);

        TreeMap<String, Integer> counter = new TreeMap<>();
        HashSet<String> names = (HashSet<String>) helper.getNameInRange(startDay, endDay);
        for (String name : names) {
            counter.put(name, helper.getCountWorkingHoursInRange(name, startDay, endDay));
        }

        model.put("repos", counter);

        return "inmonth";
    }
}

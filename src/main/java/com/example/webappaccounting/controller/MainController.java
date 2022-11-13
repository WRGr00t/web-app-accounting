package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.service.ParseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
                       Map<String, Object> model) throws IOException {
        Iterable<Shift> shiftIterable = shiftRepo.findAll();
        ArrayList<Shift> list = new ArrayList<>();
        ArrayList<Shift> nightShift = new ArrayList<>();
        ParseHelper helper = new ParseHelper(shiftRepo);

        if (!shiftIterable.iterator().hasNext()) {
            //String path = "src/main/java/com/example/webappaccounting/graf.csv";
            String path = UPLOAD_DIR;
            helper.ParseRecordCsv(path);
        }
        LocalDateTime requestDate = helper.StringToLocalDateTime(date);

        for (Shift s : shiftIterable) {
            if (s.getShiftDate().isAfter(requestDate.minusMinutes(1)) &&
                    s.getShiftDate().isBefore(requestDate.plusMinutes(1)) &&
                    isShiftTime(s.getDescription())) {
                if (isNightShift(s)) {
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
        HashSet<String> names = (HashSet<String>) getNameInMonth(11);
        for (String name : names) {
            System.out.println(name + " " + getCountWorkingHoursByMonth(name, 11, 2022));
        }

        return "inshift";
    }

    private boolean isShiftTime(String description) {
        return Pattern.matches("^\\d{1,2}\\-\\d{1,2}$", description);
    }

    private boolean isNightShift (Shift shift) {
        String description = shift.getDescription();
        String[] hours = description.split("-");
        return Integer.parseInt(hours[0]) > Integer.parseInt(hours[1]);
    }

    private int getCountWorkingHoursByMonth (String employeeName, int monthNumber, int year) {
        LocalDate init = LocalDate.of(year, monthNumber, 1);

        LocalDateTime startMonth = LocalDateTime.of(year, monthNumber, 1, 0,0,0);
        LocalDateTime endMonth = LocalDateTime.of(year, monthNumber, init.lengthOfMonth(), 23,59,59);
        ArrayList<Shift> shiftsByName = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(employeeName, startMonth, endMonth);
        int count = 0;
        for (Shift shift : shiftsByName) {
            String desc = shift.getDescription();
            if (isShiftTime(desc)) {
                String[] times = desc.split("-");
                int end = Integer.parseInt(times[1]);
                int start = Integer.parseInt(times [0]);
                if (start < end) {
                    count = count + end - start;
                } else {
                    if (shift.getShiftDate().isBefore(endMonth.minusDays(1).plusHours(1))) {
                        count = count + 24 - start;
                    } else {
                        count = count + 24 - (start - end);
                    }
                }
            }
        }
        return count;
    }

    private Set<String> getNameInMonth (int monthNumber) {
        HashSet<String> resultSet = new HashSet<>();
        LocalDate init = LocalDate.of(LocalDate.now().getYear(), monthNumber, 1);

        LocalDateTime startMonth = LocalDateTime.of(LocalDate.now().getYear(), monthNumber, 1, 0,0,0);
        LocalDateTime endMonth = LocalDateTime.of(LocalDate.now().getYear(), monthNumber, init.lengthOfMonth(), 23,59,59);
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startMonth, endMonth);
        resultSet = (HashSet<String>) shifts.stream().map(Shift::getName).collect(Collectors.toSet());
        return resultSet;
    }
}

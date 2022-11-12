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
        LocalDateTime startMonth = LocalDateTime.of(2022, 10, 1, 0,0,0);
        LocalDateTime endMonth = LocalDateTime.of(2022, 10, 31, 23,59,59);
        ArrayList<Shift> shiftsByName = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween("Котельников В.", startMonth, endMonth);
        int count = 0;
        for (Shift shift : shiftsByName) {
            System.out.println(shift.getDescription());
            System.out.println(shift.getShiftDate());
            String desc = shift.getDescription();
            String[] times = desc.split("-");
            int end = Integer.parseInt(times[1]);
            int start = Integer.parseInt(times [0]);
            if (start < end) {
                count = count + end - start;
            } else {
                count = count + 24 - (start - end);
            }
            System.out.println(count);
        }
        System.out.println(count);
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
}

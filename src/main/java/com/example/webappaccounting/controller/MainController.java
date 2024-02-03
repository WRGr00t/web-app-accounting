package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.response.PersonalResponse;
import com.example.webappaccounting.response.ReportResponse;
import com.example.webappaccounting.response.ShiftResponse;
import com.example.webappaccounting.service.ParseHelper;
import com.example.webappaccounting.service.ShiftServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@org.springframework.stereotype.Controller
public class MainController {
    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ShiftServiceImpl service;

    @Autowired
    private ParseHelper helper;

    @Value("${upload.path}")
    private String UPLOAD_DIR;

    private LocalDate startRange;
    private LocalDate endRange;

    @GetMapping("/")
    public String home(Map<String, Object> model) throws IOException {

        DateTimeFormatter dft = DateTimeFormatter
                .ofPattern("dd.MM.yyyy, HH:mm:ss");
        File f = new File(UPLOAD_DIR + "load.log");
        Path p = f.toPath();

        BasicFileAttributes attr = Files.readAttributes(p, BasicFileAttributes.class);
        FileTime fileTime = attr.lastModifiedTime();
        LocalDateTime localDateTime = fileTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        String date = localDateTime.format(dft);
        model.put("date", date);
        return "home";
    }

    @GetMapping("/inshift")
    public String main(@RequestParam(name="calendar", required=false) String date,
                       Map<String, Object> model) {
        Iterable<Shift> shiftIterable = shiftRepo.findAll();

        ArrayList<Shift> dayShift = new ArrayList<>();
        ArrayList<Shift> shift52 = new ArrayList<>();
        ArrayList<Shift> nightShift = new ArrayList<>();

        LocalDateTime requestDate = helper.StringToLocalDateTime(date);

        for (Shift s : shiftIterable) {
            if (s.getShiftDate().atStartOfDay().isAfter(requestDate.minusMinutes(1)) &&
                    s.getShiftDate().atStartOfDay().isBefore(requestDate.plusMinutes(1)) &&
                    helper.isShiftTime(s.getDescription())) {
                if (helper.isNightShift(s)) {
                    nightShift.add(s);
                } else if (s.getShiftType().equals("8*5")) {
                    shift52.add(s);
                } else {
                    dayShift.add(s);
                }
            }
        }
        Collections.sort(dayShift);
        LocalDate localDate = requestDate.toLocalDate();
        LocalDate today = LocalDate.now();
        startRange = shiftRepo.findMinimum();
        endRange = shiftRepo.findMaximum();
        model.put("startYear", startRange);
        model.put("endYear", endRange);
        model.put("today", today);
        model.put("date", localDate);
        model.put("offices", shift52);
        model.put("days", dayShift);
        model.put("nights", nightShift);

        return "inshift";
    }

    @GetMapping("/inmonth")
    public String month(@RequestParam(name="start", required=false) String start,
                        @RequestParam(name="end", required=false) String end,
                       Map<String, Object> model) {

        if (start == null || end == null) {
            LocalDate initial = LocalDate.now();
            start = String.valueOf(initial.withDayOfMonth(1));
            end = String.valueOf(initial.withDayOfMonth(initial.lengthOfMonth()));
        }

        LocalDate startDay = helper.getDateFromString(start);
        LocalDate endDay = helper.getDateFromString(end);
        startRange = shiftRepo.findMinimum();
        endRange = shiftRepo.findMaximum();

        model.put("startYear", startRange);
        model.put("endYear", endRange);

        model.put("dateStart", startDay);
        model.put("dateEnd", endDay);

        TreeSet<ReportResponse> responses = new TreeSet<>();
        HashSet<String> names = (HashSet<String>) helper.getNameInRange(startDay, endDay);
        for (String name : names) {
            responses.add(helper.getCountWorkingHoursInRange(name, startDay, endDay));
        }



        model.put("repos", responses);

        return "inmonth";
    }

    @GetMapping("/distrib")
    public String distrib(@RequestParam(name="start", required=false) String start,
                        @RequestParam(name="end", required=false) String end,
                        Map<String, Object> model) {

        if (start == null || end == null) {
            start = LocalDate.now().toString();
            end = LocalDate.now().plusWeeks(1).toString();
        }

        LocalDate startDay = helper.getDateFromString(start);
        LocalDate endDay = helper.getDateFromString(end);
        startRange = shiftRepo.findMinimum();
        endRange = shiftRepo.findMaximum();

        model.put("startYear", startRange);
        model.put("endYear", endRange);

        model.put("dateStart", startDay);
        model.put("dateEnd", endDay);

        ArrayList<ShiftResponse> responses = new ArrayList<>();
        LocalDate day = startDay;
        do {
            ShiftResponse response = new ShiftResponse();
            ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(
                    day,
                    day);
            int dayCount = 0;
            int nightCount = 0;

            for (Shift shift : shifts) {
                if (helper.isShiftTime(shift.getDescription())) {
                    if (helper.isNightShift(shift)) {
                        nightCount++;
                    } else {
                        dayCount++;
                    }
                }
            }

            String date = String.format("%td.%tm.%tY", day, day, day);
            response.setDate(date);
            Locale localeRu = new Locale("ru", "RU");
            response.setDayOfWeek(day.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, localeRu));
            response.setDayShiftCount(dayCount);
            response.setNightShiftCount(nightCount);
            responses.add(response);
            day = day.plusDays(1);
        } while (day.isBefore(endDay));

        model.put("repos", responses);

        return "distrib";
    }

    @GetMapping("/forpersonal")
    public String forpersonal(@RequestParam(name="start", required=false) String start,
                          @RequestParam(name="end", required=false) String end,
                              @RequestParam(name="person", required=false) String person,
                          Map<String, Object> model) {
        if (start == null || end == null) {
            start = LocalDate.now().toString();
            end = LocalDate.now().plusWeeks(1).toString();
        }
        if (person == null) {
            person = "";
        }
        model.put("select", person);

        LocalDate startDay = helper.getDateFromString(start);
        LocalDate endDay = helper.getDateFromString(end);
        //int month = LocalDate.now().getMonthValue();
        HashSet<String> persons = (HashSet<String>) helper.getNameInRangeWithout85(
                startDay.minusMonths(2),
                endDay.plusMonths(2));
        ArrayList<String> names = (ArrayList<String>) persons.stream()
                .sorted()
                .collect(Collectors.toList());

        startRange = shiftRepo.findMinimum();
        endRange = shiftRepo.findMaximum();

        model.put("startYear", startRange);
        model.put("endYear", endRange);

        model.put("dateStart", startDay);
        model.put("dateEnd", endDay);

        model.put("persons", names);

        TreeSet<PersonalResponse> responses = new TreeSet<>();

        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(
                person,
                startDay,
                endDay);
        for (Shift shift : shifts) {
            if (helper.isShiftTime(shift.getDescription())) {
                PersonalResponse response = new PersonalResponse();
                LocalDate day = shift.getShiftDate();
                String date = String.format("%td.%tm.%tY", day, day, day);
                response.setDate(date);
                Locale localeRu = new Locale("ru", "RU");
                response.setDayOfWeek(day.getDayOfWeek()
                        .getDisplayName(TextStyle.FULL, localeRu));
                response.setDescription(shift.getDescription());

                responses.add(response);
            }
        }
        model.put("repos", responses);
        return "forpersonal";
    }

    @GetMapping("/news")
    public String getNews(Map<String, Object> model) throws IOException {
        int logSize = 200;
        String pathToLog = UPLOAD_DIR + "load.log";
        Path path = Paths.get(pathToLog);
        ArrayList<String> read = (ArrayList<String>) Files.readAllLines(path);
        if (read.size() < logSize) {
            logSize = read.size();
        }
        model.put("messages", read.stream()
                .limit(logSize)
                .toArray());
        model.put("limit", String.format("Отображается %d записей лога", logSize));
        return "news";
    }
}

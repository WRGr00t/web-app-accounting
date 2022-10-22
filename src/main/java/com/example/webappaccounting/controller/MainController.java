package com.example.webappaccounting.controller;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Controller
public class MainController {
    @Autowired
    private ShiftRepo shiftRepo;

    @GetMapping("/")
    public String home(Map<String, Object> model){
        LocalDate day = LocalDate.now();
        model.put("today", day);
        day = LocalDate.now().plusDays(1);
        model.put("tomorrow", day);
        return "home";
    }

    @GetMapping("/greeting")
    public String greeting(Map<String, Object> model) throws Exception {
        Iterable<Shift> shiftIterable = shiftRepo.findAll();
        if (!shiftIterable.iterator().hasNext()) {
            String path = "src/main/java/com/example/webappaccounting/graf.csv";
            ParseRecordCsv(path);
        }
        model.put("name", shiftIterable.iterator());
        return "greeting";
    }
    @GetMapping("/inshift")
    public String main(@RequestParam(name="calendar", required=false) String date, Map<String, Object> model) throws IOException {
        Iterable<Shift> shiftIterable = shiftRepo.findAll();
        ArrayList<Shift> list = new ArrayList<>();
        ArrayList<Shift> nightShift = new ArrayList<>();

        if (!shiftIterable.iterator().hasNext()) {
            String path = "src/main/java/com/example/webappaccounting/graf.csv";
            ParseRecordCsv(path);
        }
        LocalDateTime requestDate = LocalDateTime.now();

        shiftIterable = shiftRepo.findAll();
        if (date == null) {
            date = String.valueOf(LocalDate.now());
        }
            try {
                String[] strings = date.split("-");
                requestDate = LocalDateTime.of(
                        Integer.parseInt(strings[0]),
                        Integer.parseInt(strings[1]),
                        Integer.parseInt(strings[2]), 0, 0, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        model.put("repo", list);
        model.put("night", nightShift);
        return "inshift";
    }

    private Set<Shift> ParseRecordCsv(String filePath) throws IOException {
        //Загружаем строки из файла
        List<String> fileLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        String currentMonth = "";
        String currentYear = "";
        String currentType = "";
        TreeSet<Shift> shifts = new TreeSet<>();

        for (String fileLine : fileLines) {
            if (isNewMonth(fileLine)) {
                int monthPosition = 3;
                int yearPosition = 4;
                String[] words = fileLine.split(" ");
                currentMonth = words[monthPosition].toLowerCase();
                currentYear = words[yearPosition];
            }
            String[] splitedText = fileLine.split(";");
            ArrayList<String> columnList = new ArrayList<>();
            for (String s : splitedText) {
                //Если колонка начинается на кавычки или заканчиваеться на кавычки
                if (IsColumnPart(s)) {
                    String lastText = columnList.get(columnList.size() - 1);
                    columnList.set(columnList.size() - 1, lastText + ";" + s);
                } else {
                    columnList.add(s);
                }
            }
            ArrayList<Shift> shiftList = new ArrayList<>();

            if (columnList.size() > 0) {
                int namePosition = 0;
                int shiftBeginPosition = 3;
                int typePosition = 1;
                String text = columnList.get(namePosition);

                if (IsName(text)) {
                    String type = columnList.get(typePosition);
                    if (!type.isEmpty()) {
                        currentType = type;
                    }
                    for (int i = shiftBeginPosition; i < columnList.size(); i++) {
                        String currentColumn = columnList.get(i);
                        if (!currentColumn.isEmpty()) {
                            LocalDateTime dateShift = LocalDateTime.of(
                                    Integer.parseInt(currentYear),
                                    getNumberOfMonth(currentMonth),
                                    (i - shiftBeginPosition + 1),
                                    0, 0, 0);
                            Shift shift = new Shift(dateShift,
                                    currentColumn,
                                    text,
                                    currentType);
                            //shiftList.add(shift);
                            //shifts.add(shift);
                            if (shiftRepo.findAllByShiftDateAndDescriptionAndNameAndShiftType(
                                    shift.getShiftDate(),
                                            shift.getDescription(),
                                            shift.getName(),
                                            shift.getShiftType())
                                    .isEmpty()){
                                shiftRepo.save(shift);
                            }
                        }
                    }
                }
            }
        }

        /*for (Shift shift : shifts) {
            if (shiftRepo.findAllByShiftDateAndDescriptionAndName(
                    shift.getShiftDate(),
                    shift.getDescription(),
                    shift.getName())
                    .isEmpty()) {
                System.out.println(shift);
                shiftRepo.save(shift);
            }
        }*/

        return shifts;
    }

    //Проверка является ли колонка частью предыдущей колонки
    private static boolean IsColumnPart(String text) {
        String trimText = text.trim();
        //Если в тексте одна ковычка и текст на нее заканчиваеться значит это часть предыдущей колонки
        return trimText.indexOf("\"") == trimText.lastIndexOf("\"") && trimText.endsWith("\"");
    }

    private static boolean IsName(String text) {
        Pattern pattern = Pattern.compile("([А-ЯЁ][а-яё]+[\\-\\s]?\\s[А-ЯЁ]?\\.?$)");
        Matcher matcher;
        matcher = pattern.matcher(text);
        return matcher.find();
    }

    private static int getNumberOfMonth(String string) {
        int number = 0;
        string = string.toLowerCase();
        switch (string) {
            case "январь":
                number = 1;
                break;
            case "февраль":
                number = 2;
                break;
            case "март":
                number = 3;
                break;
            case "апрель":
                number = 4;
                break;
            case "май":
                number = 5;
                break;
            case "июнь":
                number = 6;
                break;
            case "июль":
                number = 7;
                break;
            case "август":
                number = 8;
                break;
            case "сентябрь":
                number = 9;
                break;
            case "октябрь":
                number = 10;
                break;
            case "ноябрь":
                number = 11;
                break;
            case "декабрь":
                number = 12;
                break;
        }
        return number;
    }

    private static boolean isFindNewMonth(String line) {
        return Pattern.compile(Pattern.quote("ГРАФИК"), Pattern.CASE_INSENSITIVE).matcher(line).find();
    }

    private static boolean isNewMonth(String string) {
        String substring = "ГРАФИК РАБОТЫ НА";
        substring = substring.toLowerCase();
        return string.toLowerCase().contains(substring);
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

package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.response.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ParseHelper {

    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ShiftServiceImpl service;

    @Value("${upload.path}")
    private String UPLOAD_DIR;

    public ParseHelper(ShiftRepo shiftRepo, ShiftServiceImpl service) {
        this.shiftRepo = shiftRepo;
        this.service = service;
    }

    public Set<Shift> ParseRecordCsv(String filePath) throws IOException {
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
                            if (shiftRepo.findAllByShiftDateAndDescriptionAndNameAndShiftType(
                                            shift.getShiftDate(),
                                            shift.getDescription(),
                                            shift.getName(),
                                            shift.getShiftType())
                                    .isEmpty()){
                                service.save(shift);
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

    private static boolean isFindNewMonth(String line) {
        return Pattern.compile(Pattern.quote("ГРАФИК"), Pattern.CASE_INSENSITIVE).matcher(line).find();
    }

    private static boolean isNewMonth(String string) {
        String substring = "ГРАФИК РАБОТЫ НА";
        substring = substring.toLowerCase();
        return string.toLowerCase().contains(substring);
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

    public LocalDateTime StringToLocalDateTime(String date) {
        LocalDateTime result = LocalDateTime.now();
        if (date == null) {
            date = String.valueOf(LocalDate.now());
        }
        try {
            String[] strings = date.split("-");
            result = LocalDateTime.of(
                    Integer.parseInt(strings[0]),
                    Integer.parseInt(strings[1]),
                    Integer.parseInt(strings[2]), 0, 0, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
    public boolean isShiftTime(String description) {
        return Pattern.matches("^\\d{1,2}\\-\\d{1,2}$", description);
    }

    public boolean isNightShift(Shift shift) {
        String description = shift.getDescription();
        String[] hours = description.split("-");
        return Integer.parseInt(hours[0]) > Integer.parseInt(hours[1]);
    }

    public int getCountWorkingHoursByMonth(String employeeName, int monthNumber, int year) {
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
                    if (shift.getShiftDate().isAfter(endMonth.minusDays(1))) {
                        count = count + 24 - start;
                    } else {
                        count = count + 24 - (start - end);
                    }
                }
            }
        }
        return count;
    }

    public ReportResponse getCountWorkingHoursInRange(String employeeName, LocalDate startRange, LocalDate endRange) {
        ReportResponse response = new ReportResponse();
        LocalDateTime startTime = startRange.atStartOfDay();
        LocalDateTime finishTime = endRange.atTime(23, 59, 59);
        ArrayList<Shift> shiftsByName = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(employeeName, startTime, finishTime);
        int countHours = 0;
        int countShifts = 0;
        int countShiftsWithoutDinner = 0;
        for (Shift shift : shiftsByName) {
            String desc = shift.getDescription();
            if (isShiftTime(desc)) {
                countShifts++;
                String[] times = desc.split("-");
                int end = Integer.parseInt(times[1]);
                int start = Integer.parseInt(times [0]);
                if (start < end) {
                    int duration = end - start;
                    if (duration <= 4) {
                        countShiftsWithoutDinner++;
                    }
                    countHours = countHours + duration;
                } else {
                    if (shift.getShiftDate().isAfter(finishTime.minusDays(1))) {
                        countHours = countHours + 24 - start;
                    } else {
                        countHours = countHours + 24 - (start - end);
                    }
                }
            }
        }
        response.setName(employeeName);
        response.setCountHours(countHours);
        response.setCountShifts(countShifts);
        response.setCountShiftsWithoutDinner(countShiftsWithoutDinner);
        return response;

    }

    public Set<String> getNameInMonth(int monthNumber) {
        Set<String> resultSet;
        LocalDate init = LocalDate.of(LocalDate.now().getYear(), monthNumber, 1);

        LocalDateTime startMonth = LocalDateTime.of(LocalDate.now().getYear(), monthNumber, 1, 0,0,0);
        LocalDateTime endMonth = LocalDateTime.of(LocalDate.now().getYear(), monthNumber, init.lengthOfMonth(), 23,59,59);
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startMonth, endMonth);
        resultSet = shifts.stream()
                .map(Shift::getName)
                .collect(Collectors.toSet());
        return resultSet;
    }

    public Set<String> getNameInRange(LocalDate startRange, LocalDate endRange) {
        Set<String> resultSet;
        LocalDateTime startMonth = startRange.atStartOfDay();
        LocalDateTime endMonth = endRange.atTime(23, 59, 59);
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startMonth, endMonth);
        resultSet = shifts.stream()
                .map(Shift::getName)
                .collect(Collectors.toSet());
        return resultSet;
    }

    public Set<String> getNameInRangeWithout85(LocalDate startRange, LocalDate endRange) {
        LocalDateTime startMonth = startRange.atStartOfDay();
        LocalDateTime endMonth = endRange.atTime(23, 59, 59);
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startMonth, endMonth);
        Set<String> resultSet = shifts.stream()
                .filter(x -> !x.getShiftType().equals("8*5"))
                .map(Shift::getName)
                .collect(Collectors.toSet());
        return resultSet;
    }

    public LocalDate getDateFromString(String date) {
        if (!date.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            formatter = formatter.withLocale(Locale.ROOT);
            return LocalDate.parse(date, formatter);
        } else {
            return LocalDate.now();
        }
    }
}

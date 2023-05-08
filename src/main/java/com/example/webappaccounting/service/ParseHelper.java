package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.model.ShiftNative;
import com.example.webappaccounting.model.Status;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.response.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
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

    public void ParseRecordCsv(String filePath) throws IOException {
        //Загружаем строки из файла
        StringBuilder log = new StringBuilder();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        ArrayList<Shift> shiftsFromDB = (ArrayList<Shift>) shiftRepo.findAll();
        String currentMonth = "";
        String currentYear = "";
        String currentType = "";
        ArrayList<Shift> shiftsFromCSV = new ArrayList<>();
        ArrayList<Shift> shiftsToDB = new ArrayList<>();

        for (String fileLine : fileLines) {

            if (isNewMonth(fileLine)) {
                int monthPosition = 3;
                int yearPosition = 4;
                String[] words = fileLine.split(" ");
                currentMonth = words[monthPosition].toLowerCase();
                currentYear = words[yearPosition];
            }
            //Добавляем только строчки без пустых смен на весь месяц
            String sub = fileLine.replaceFirst(
                    "^([А-ЯЁ][а-яё]+[\\-\\s]?\\s[А-Я]{1}[а-яё]{1,23}|[А-ЯЁ][а-яё]+[\\-\\s]?\\s[А-ЯЁ]?\\.?)",
                    "");
            sub = sub.replaceAll(";", "");
            ArrayList<String> columnList = new ArrayList<>();
            if (!sub.isEmpty()) {
                String[] splitedText = fileLine.split(";");
                for (String s : splitedText) {
                    //Если колонка начинается на кавычки или заканчиваеться на кавычки
                    if (IsColumnPart(s)) {
                        String lastText = columnList.get(columnList.size() - 1);
                        columnList.set(columnList.size() - 1, lastText + ";" + s);
                    } else {
                        columnList.add(s);
                    }
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
                            LocalDate dateShift = LocalDate.of(
                                    Integer.parseInt(currentYear),
                                    getNumberOfMonth(currentMonth),
                                    (i - shiftBeginPosition + 1));
                            Shift shift = new Shift(dateShift,
                                    currentColumn,
                                    text,
                                    currentType);
                            shiftsFromCSV.add(shift);
                            ArrayList<Shift> listInDB = (ArrayList<Shift>) shiftsFromDB.stream()
                                    .filter(x -> x.getShiftDate().equals(shift.getShiftDate()))
                                    .filter(x-> x.getName().equals(shift.getName()))
                                    .collect(Collectors.toList());
                            /*Optional<Shift> shiftInDB = shiftRepo.findAllByShiftDateAndName(
                                    shift.getShiftDate(),
                                    shift.getName());*/
                            if (listInDB.isEmpty()) {
                                //service.save(shift);
                                shiftsToDB.add(shift);
                            } else {
                                Shift shiftForCheck = listInDB.stream().findFirst().get();
                                if (!shiftForCheck.getDescription().equals(shift.getDescription())) {
                                    shift.setId(shiftForCheck.getId());
                                    System.out.println("update shift " + shift);
                                    log.append(String.format("изменена смена %s", shift))
                                            .append("\n");
                                    //service.save(shift);
                                    shiftsToDB.add(shift);
                                }
                            }
                            /*if (shiftRepo.findAllByShiftDateAndDescriptionAndNameAndShiftType(
                                            shift.getShiftDate(),
                                            shift.getDescription(),
                                            shift.getName(),
                                            shift.getShiftType())
                                    .isEmpty()){
                                service.save(shift);
                            }*/
                        }
                    }
                }
            }
        }
        //System.out.println("Size array shiftFromCSV = " + shiftsFromCSV.size());
        //System.out.println("Size array shiftsFromDB = " + shiftsFromDB.size());
        ArrayList<ShiftNative> shiftNativeInDB = (ArrayList<ShiftNative>) shiftsFromDB.stream()
                .map(shift -> new ShiftNative(
                        shift.getName(),
                        shift.getShiftDate(),
                        shift.getDescription()))
                .collect(Collectors.toList());
        ArrayList<ShiftNative> shiftNativeInCSV = (ArrayList<ShiftNative>) shiftsFromCSV.stream()
                .map(shift -> new ShiftNative(
                        shift.getName(),
                        shift.getShiftDate(),
                        shift.getDescription()))
                .collect(Collectors.toList());

        List<ShiftNative> differences = shiftNativeInDB.stream()
                .filter(element -> !shiftNativeInCSV.contains(element))
                .collect(Collectors.toList());

        ArrayList<Shift> differ = new ArrayList<>();
        for (ShiftNative diff : differences) {
            Optional<Shift> optionalShift = shiftRepo.findAllByNameAndShiftDateAndDescription(
                    diff.getName(),
                    diff.getShiftDate(),
                    diff.getDescription());
            optionalShift.ifPresent(differ::add);
        }
        String pathToFile =
                //"src/main/java/com/example/webappaccounting/upload/load.log";
        "/root/tmp/upload/load.log";
        for (ShiftNative shift: differences) {
            log.append(String.format("изменена (удалена) смена %s", shift))
                    .append("\n");
        }

        try {
            recordLog(pathToFile, log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(log);
        service.deleteAll(differ);
        service.saveAll(shiftsToDB);

    }

    private void recordLog(String pathToFile, String log) throws IOException {
        LocalDateTime now = LocalDateTime.now();
        File file = new File(pathToFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String logDateTime = now.format(formatter);
        log = String.format("%s %s", logDateTime, log);
        try(FileWriter writer = new FileWriter(pathToFile, true))
        {
            writer.write(log);
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
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
        //регулярное выражение на запись имени сотрудника типа Иванов И, Иванов И., Иванов Иван
        String reg = "^([А-ЯЁ][а-яё]+[\\-\\s]?\\s[А-Я]{1}[а-яё]{1,23}|[А-ЯЁ][а-яё]+[\\-\\s]?\\s[А-ЯЁ]?\\.?)$";
        Pattern pattern = Pattern.compile(reg);
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
        if (isShiftTime(shift.getDescription())) {
            String description = shift.getDescription();
            String[] hours = description.split("-");
            return Integer.parseInt(hours[0]) > Integer.parseInt(hours[1]);
        } else {
            return false;
        }

    }

    public int getCountWorkingHoursByMonth(String employeeName, int monthNumber, int year) {
        LocalDate init = LocalDate.of(year, monthNumber, 1);

        LocalDate startMonth = LocalDate.of(year, monthNumber, 1);
        LocalDate endMonth = LocalDate.of(year, monthNumber, init.lengthOfMonth());
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
        //LocalDate startTime = startRange.atStartOfDay();
        //LocalDate finishTime = endRange.atTime(23, 59, 59);
        ArrayList<Shift> shiftsByName = (ArrayList<Shift>) shiftRepo.findAllByNameAndShiftDateBetween(employeeName, startRange, endRange);
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
                    if (shift.getShiftDate().isAfter(endRange.minusDays(1))) {
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

        LocalDate startMonth = LocalDate.of(LocalDate.now().getYear(), monthNumber, 1);
        LocalDate endMonth = LocalDate.of(LocalDate.now().getYear(), monthNumber, init.lengthOfMonth());
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startMonth, endMonth);
        resultSet = shifts.stream()
                .map(Shift::getName)
                .collect(Collectors.toSet());
        return resultSet;
    }

    public Set<String> getNameInRange(LocalDate startRange, LocalDate endRange) {
        Set<String> resultSet;

        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startRange, endRange);
        resultSet = shifts.stream()
                .map(Shift::getName)
                .collect(Collectors.toSet());
        return resultSet;
    }

    public Set<String> getNameInRangeWithout85(LocalDate startRange, LocalDate endRange) {
        ArrayList<Shift> shifts = (ArrayList<Shift>) shiftRepo.findAllByShiftDateBetween(startRange, endRange);
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

    public Status getStatus(Shift shift) {
        String desc = shift.getDescription().toUpperCase();
        Status result = Status.DAYSHIFT;
        if (isNightShift(shift)) {
            result = Status.NIGHTSHIFT;
        }
        switch (desc) {
            case "О":
            case "ОТ": {
                result = Status.HOLIDAY;
                break;
            }
            case "Б": {
                result = Status.SICKDAY;
                break;
            }
        }
        return result;
    }
}

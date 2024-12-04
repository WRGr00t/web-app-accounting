package com.example.webappaccounting.service;

import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.model.ShiftNative;
import com.example.webappaccounting.model.Status;
import com.example.webappaccounting.model.Subscribe;
import com.example.webappaccounting.repository.ShiftRepo;
import com.example.webappaccounting.response.ReportResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
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

@Service
public class ParseHelper {

    @Autowired
    private ShiftRepo shiftRepo;

    @Autowired
    private ShiftServiceImpl service;

    @Autowired
    private EmailService emailService;

    private HashMap<String, ArrayList<String>> changeForSender = new HashMap<>();

    @Autowired
    private SubscribeService subscribeService;

    /*@Autowired
    private SendService senderService;*/

    @Value("${upload.path}")
    private String UPLOAD_DIR;

    public ParseHelper() {
    }

    public void ParseRecordCsv(String filePath) throws IOException {
        //Загружаем строки из файла

        StringBuilder log = new StringBuilder();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        ArrayList<Shift> shiftsFromDB = (ArrayList<Shift>) shiftRepo.findAll();
        ArrayList<String> subscribeNames = (ArrayList<String>) subscribeService.findAll().stream()
                .map(Subscribe::getUsername)
                .collect(Collectors.toList());
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

            if (!columnList.isEmpty()) {
                int namePosition = 0;
                int shiftBeginPosition = 3;
                int typePosition = 1;
                String text = columnList.get(namePosition);

                if (IsName(text)) {
                    boolean isDuty;
                    String type = columnList.get(typePosition);
                    if (!type.isEmpty()) {
                        currentType = type;
                    }
                    for (int i = shiftBeginPosition; i < columnList.size(); i++) {
                        isDuty = false;
                        String currentColumn = columnList.get(i);
                        if (!currentColumn.isEmpty()) {
                            if (isDutyShift(currentColumn)) {
                                isDuty = true;
                                currentColumn = currentColumn.replace("д", "");
                            }
                            LocalDate dateShift = LocalDate.of(
                                    Integer.parseInt(currentYear),
                                    getNumberOfMonth(currentMonth),
                                    (i - shiftBeginPosition + 1));
                            Shift shift = new Shift(dateShift,
                                    currentColumn,
                                    text,
                                    currentType,
                                    isDuty);
                            shiftsFromCSV.add(shift);

                            //Ищем такую же смену в базе
                            ArrayList<Shift> listInDB = (ArrayList<Shift>) shiftsFromDB.stream()
                                    .filter(x -> x.getShiftDate().equals(shift.getShiftDate()))
                                    .filter(x -> x.getName().equals(shift.getName()))
                                    .filter(x -> x.isDuty() == shift.isDuty())
                                    .collect(Collectors.toList());
                            ShiftNative shiftNative = new ShiftNative(
                                    shift.getName(),
                                    shift.getShiftDate(),
                                    shift.getDescription(),
                                    shift.isDuty());

                            System.out.println(listInDB.size());

                            //Если смена уникальная, добавляем
                            if (listInDB.isEmpty()) {

                                //лог изменений
                                String newShift = String.format("добавлена смена %s",
                                        shiftNative.printShift());

                                //если изменена смена подписанных на рассылку
                                if (subscribeNames.contains(shift.getName())) {
                                    putToChangeForSubscribe(shift.getName(), newShift);
                                }

                                log.append(newShift)
                                        .append("\n");
                                shiftsToDB.add(shift);
                                //если такая смена нашлась
                            } else {
                                Shift shiftForCheck = listInDB.stream().findFirst().get();
                                if (!shiftForCheck.getDescription().equals(shift.getDescription()) ||
                                        (shiftForCheck.isDuty() ^ shift.isDuty())) {
                                    shift.setId(shiftForCheck.getId());

                                    String changeShift = String.format("изменена смена %s",
                                            shiftNative.printShift());
                                    if (subscribeNames.contains(shift.getName())) {
                                        putToChangeForSubscribe(shift.getName(), changeShift);
                                    }
                                    log.append(changeShift)
                                            .append("\n");
                                    shiftsToDB.add(shift);
                                }
                            }
                        }
                    }
                }
            }
        }
        // берем все смены в базе
        ArrayList<ShiftNative> shiftNativeInDB = (ArrayList<ShiftNative>) shiftsFromDB.stream()
                .map(shift -> new ShiftNative(
                        shift.getName(),
                        shift.getShiftDate(),
                        shift.getDescription(),
                        shift.isDuty()))
                .collect(Collectors.toList());
        // берем все смены в файле
        ArrayList<ShiftNative> shiftNativeInCSV = (ArrayList<ShiftNative>) shiftsFromCSV.stream()
                .map(shift -> new ShiftNative(
                        shift.getName(),
                        shift.getShiftDate(),
                        shift.getDescription(),
                        shift.isDuty()))
                .collect(Collectors.toList());
        // складываем в массив смены из базы, которых нет в файле, только по текущему году загрузки
        List<ShiftNative> differences = shiftNativeInDB.stream()
                .filter(shiftNative -> shiftNative.getShiftDate().getYear() == shiftNativeInCSV.get(0).getShiftDate().getYear())
                .filter(element -> !shiftNativeInCSV.contains(element))
                .collect(Collectors.toList());
        // Из различий в базе выбираем в массив смены по сотруднику, дате и описанию
        ArrayList<Shift> differ = new ArrayList<>();
        for (ShiftNative diff : differences) {
            Optional<Shift> optionalShift = shiftRepo.findAllByNameAndShiftDateAndDescriptionAndIsDuty(
                    diff.getName(),
                    diff.getShiftDate(),
                    diff.getDescription(),
                    diff.isDuty());
            optionalShift.ifPresent(differ::add);
        }
        String pathToFile = UPLOAD_DIR + "load.log";
                //"src/main/java/com/example/webappaccounting/upload/load.log";
        //"/root/tmp/upload/load.log";

        // Удаляем все смены, которых больше нет в файле графика и формируем лог
        for (ShiftNative shift: differences) {
            String deleteShift = String.format("удалена смена %s", shift.printShift());
            if (subscribeNames.contains(shift.getName())) {
                putToChangeForSubscribe(shift.getName(), deleteShift);
            }
            log.append(deleteShift)
                    .append("\n");
        }

        // итоговая рассылка по изменениям
        String resultLog = log.toString();
        //System.out.println(resultLog);
        if (!resultLog.isEmpty()) {
            sendMail();
            try {
                recordLog(pathToFile, resultLog);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No change");
        }

        service.deleteAll(differ);
        service.saveAll(shiftsToDB);

    }

    private void recordLog(String pathToFile, String log) throws IOException {

        File file = new File(pathToFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        StringBuilder result = new StringBuilder();
        result.append(setLogDate())
                .append(log);

        FileReader fr= new FileReader(pathToFile);
        Scanner scan = new Scanner(fr);
        StringBuilder builder = new StringBuilder();
        while (scan.hasNextLine()) {
            builder.append(scan.nextLine())
                    .append('\n');
        }
        result.append(builder);
        fr.close();

        //System.out.println(result);

        try(FileWriter writer = new FileWriter(pathToFile, false))
        {
            writer.write(result.toString());
            writer.append('\n');
            writer.flush();
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    private String setLogDate() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        return now.format(formatter) + "\n";
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

    public boolean isDutyShift(String desc) {
        return Pattern.matches("^\\d{1,2}\\-\\d{1,2}+[д]{1}$", desc);
    }

    public Status getTypeShift(String description) {
        if (isShiftTime(description)) {
            String[] hours = description.split("-");
            int duration = Integer.parseInt(hours[1]) - Integer.parseInt(hours[0]);
            if (duration > 12) {
                return Status.HARD;
            } else if (duration < 12) {
                return Status.LIGHT;
            } else {
                return Status.DAYSHIFT;
            }
        } else {
            return Status.NOTDEFINE;
        }
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
        return shifts.stream()
                .filter(x -> !x.getShiftType().equals("8*5"))
                .map(Shift::getName)
                .collect(Collectors.toSet());
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
        Status result = getTypeShift(desc);
        if (isNightShift(shift)) {
            return Status.NIGHTSHIFT;
        }
        if (shift.isDuty()) {
            return Status.DUTYSHIFT;
        }
        return switch (desc) {
            case "О", "ОТ" -> Status.HOLIDAY;
            case "Б" -> Status.SICKDAY;
            case "У", "УВ" -> Status.DISMISSAL;
            case "К", "K" -> Status.BTRIP;
            default -> result;
        };
    }

    private void sendMail() {

        for (String key : changeForSender.keySet()) {

            String subject = String.format("Изменения в графике %s", setLogDate());
            ArrayList<String> emails = getEmailsForName(key);
            StringBuilder builder = new StringBuilder();
            ArrayList<String> changes = changeForSender.get(key);
            for (String change : changes) {
                builder.append(change)
                        .append("\n");
            }
            for (String email : emails) {
                emailService.sendSimpleMessage(email, subject, builder.toString());
            }
        }
        changeForSender.clear(); //clear after send
    }

    private ArrayList<String> getEmailsForName(String name) {
        ArrayList<Subscribe> subscribes = subscribeService.findAllMailByUsername(name);
        return (ArrayList<String>) subscribes.stream()
                .map(Subscribe::getEmail)
                .collect(Collectors.toList());
    }

    public boolean patternMatches(String emailAddress) {
        String regexPattern =
                //"^[A-Za-z0-9+_.-]+@(.+)$";
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return Pattern.compile(regexPattern)
                .matcher(emailAddress)
                .matches();
    }

    private void putToChangeForSubscribe (String subscribeName, String change) {
        if (changeForSender.containsKey(subscribeName)) {
            ArrayList<String> payloads = changeForSender.get(subscribeName);
            payloads.add(change);
            changeForSender.replace(subscribeName, payloads);
        } else {
            changeForSender.put(subscribeName, new ArrayList<>(List.of(change)));
        }
    }

    public ArrayList<String> getAvailableYears() {
        ArrayList<String> result = new ArrayList<>();
        int startYear = shiftRepo.findMinimum().getYear();
        int finishYear = shiftRepo.findMaximum().getYear();
        for (int i = startYear; i <= finishYear; i++) {
            result.add(String.valueOf(i));
        }
        return result;
    }

    public String getShiftsList(Iterable<Shift> shiftIterable) {
        StringBuilder dayShift = new StringBuilder();
        StringBuilder nightShift = new StringBuilder();
        StringBuilder duty = new StringBuilder();
        for (Shift s : shiftIterable) {
            if (s.isDuty()) {
                duty.append(s.getName())
                        .append("\n");
            }
            if (isShiftTime(s.getDescription()) && !s.getShiftType().equals("8*5")) {
                if (isNightShift(s)) {
                    nightShift.append(s.getName())
                            .append("\n");
                } else {
                    dayShift.append(s.getName())
                            .append("\n");
                }
            }
        }
        dayShift
                .append("Дежурная смена:\n")
                .append(duty)
                .append("В ночь:\n")
                .append(nightShift);
        return dayShift.toString().trim();
    }
}

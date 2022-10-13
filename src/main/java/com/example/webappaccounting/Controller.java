package com.example.webappaccounting;

import com.example.webappaccounting.model.Employee;
import com.example.webappaccounting.model.Shift;
import com.example.webappaccounting.repository.EmployeeRepo;
import com.example.webappaccounting.repository.ShiftRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Controller
public class Controller {
    @Autowired
    private ShiftRepo shiftRepo;
    @Autowired
    private EmployeeRepo employeeRepo;

    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Map<String, Object> model) throws Exception {
        String path = "src/main/java/com/example/webappaccounting/graf.csv";
        List<Record> recordList = ParseRecordCsv(path);

        for (Record record : recordList) {
            System.out.println(record.toString());
        }
        model.put("name", recordList.size());
        return "greeting";
    }

    public String main(Map<String, Object> model) {
        //Iterable<Shift> shifts = shiftRepo.findAll();
        //model.put("shifts", shifts);

        return "main";
    }

    private List<Record> ParseRecordCsv(String filePath) throws IOException {
        //Загружаем строки из файла
        List<Record> records = new ArrayList<>();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        String currentMonth = "";
        String currentYear = "";
        String currentType = "";
        TreeSet<Employee> employees = new TreeSet<>();
        TreeSet<Shift> shifts = new TreeSet<>();

        for (String fileLine : fileLines) {
            if (isNewMonth(fileLine)) {
                System.out.println("yes - " + fileLine);
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
            Record record = new Record();
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
                    record.setName(text);
                    for (int i = shiftBeginPosition; i < columnList.size(); i++) {
                        String currentColumn = columnList.get(i);
                        if (!currentColumn.isEmpty()) {
                            LocalDateTime dateShift = LocalDateTime.of(
                                    Integer.parseInt(currentYear),
                                    getNumberOfMonth(currentMonth),
                                    (i - shiftBeginPosition + 1),
                                    0, 0, 0);
                            Employee employee = new Employee(text, currentType);
                            Shift shift = new Shift(dateShift,
                                    currentColumn,
                                    employee);
                            shiftList.add(shift);
                            shifts.add(shift);
                            employees.add(employee);
                        }
                    }
                    record.setDateList(shiftList);
                    records.add(record);

                }
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Сегодня в смене:")
                .append("\n");
        for (Shift shift : shifts) {
            if (shift.getShiftDate().isAfter(LocalDateTime.now().minusDays(1)) && shift.getShiftDate().isBefore(LocalDateTime.now())){
                stringBuilder.append(shift.getEmployee().getName())
                        .append(" ")
                        .append(shift.getDescription())
                        .append("\n");
            }
        }
        System.out.println(stringBuilder);

        for (Shift shift : shifts) {
            shiftRepo.save(shift);
        }
        for (Employee employee : employees) {
            employeeRepo.save(employee);
        }
        return records;
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
}

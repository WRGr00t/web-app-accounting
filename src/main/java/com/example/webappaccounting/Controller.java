package com.example.webappaccounting;

import com.opencsv.CSVReader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Controller
public class Controller {
    @GetMapping("/greeting")
    public String greeting(@RequestParam(name="name", required=false, defaultValue="World") String name, Map<String, Object> model) throws Exception {
        String path = "src/main/java/com/example/webappaccounting/graf.csv";
        List<Record> recordList = ParseRecordCsv(path);

        for (Record record : recordList) {
            System.out.println(record.toString());
        }
        model.put("name", recordList.get(5).getName());
        return "greeting";
    }

    private static List<Record> ParseRecordCsv(String filePath) throws IOException {
        //Загружаем строки из файла
        List<Record> records = new ArrayList<>();
        List<String> fileLines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
        for (String fileLine : fileLines) {
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
            ArrayList<String> shiftList = new ArrayList<>();
            if (columnList.size() > 0) {
                int namePosition = 0;
                int shiftBeginPosition = 3;
                record.setName(columnList.get(namePosition));
                for (int i = shiftBeginPosition; i < columnList.size(); i++) {
                    shiftList.add((i - shiftBeginPosition + 1) + " - " + columnList.get(i));
                }
            }
            record.setDateList(shiftList);
            records.add(record);
        }
        return records;
    }

    //Проверка является ли колонка частью предыдущей колонки
    private static boolean IsColumnPart(String text) {
        String trimText = text.trim();
        //Если в тексте одна ковычка и текст на нее заканчиваеться значит это часть предыдущей колонки
        return trimText.indexOf("\"") == trimText.lastIndexOf("\"") && trimText.endsWith("\"");
    }
}

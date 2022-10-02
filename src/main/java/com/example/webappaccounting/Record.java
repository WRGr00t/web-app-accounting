package com.example.webappaccounting;

import java.util.ArrayList;

public class Record {
    String name;
    ArrayList<String> dateList;

    public Record(String name, ArrayList<String> dateList) {
        this.name = name;
        this.dateList = dateList;
    }

    public Record() {
        this.name = "";
        this.dateList = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDateList() {
        return dateList;
    }

    public void setDateList(ArrayList<String> dateList) {
        this.dateList = dateList;
    }

    @Override
    public String toString() {
        StringBuilder dates = new StringBuilder();
        for (String str : dateList) {
            dates.append(str);
            dates.append(", ");
        }

        return "Record{" +
                "name='" + name + '\'' +
                ", shift=" + dates +
                '}';
    }
}

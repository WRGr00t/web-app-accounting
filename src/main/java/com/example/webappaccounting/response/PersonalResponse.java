package com.example.webappaccounting.response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PersonalResponse implements Comparable<PersonalResponse>{
    private String date;
    private String dayOfWeek;

    private String description;

    public PersonalResponse() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int compareTo(PersonalResponse o) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String dateO = o.getDate();

        //convert String to LocalDate
        LocalDate localDateO = LocalDate.parse(dateO, formatter);
        LocalDate localDateThis = LocalDate.parse(date, formatter);

        return localDateThis.compareTo(localDateO);
    }
}

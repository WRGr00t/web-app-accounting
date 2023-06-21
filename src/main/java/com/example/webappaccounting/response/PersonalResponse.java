package com.example.webappaccounting.response;

import java.time.LocalDate;

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
        return date.compareTo(o.getDate());
    }
}

package com.example.webappaccounting.response;

import java.time.LocalDate;

public class ShiftResponse {
    private LocalDate date;
    private String dayOfWeek;
    private int dayShiftCount;
    private int nightShiftCount;

    public ShiftResponse() {
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayShiftCount() {
        return dayShiftCount;
    }

    public void setDayShiftCount(int dayShiftCount) {
        this.dayShiftCount = dayShiftCount;
    }

    public int getNightShiftCount() {
        return nightShiftCount;
    }

    public void setNightShiftCount(int nightShiftCount) {
        this.nightShiftCount = nightShiftCount;
    }
}

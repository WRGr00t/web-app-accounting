package com.example.webappaccounting.response;

import java.time.LocalDate;

public class CalendarResponse {
    private LocalDate date;
    private boolean night;

    public CalendarResponse() {
    }

    public CalendarResponse(LocalDate date, boolean night) {
        this.date = date;
        this.night = night;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
    }
}

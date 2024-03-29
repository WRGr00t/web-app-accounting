package com.example.webappaccounting.response;

import com.example.webappaccounting.model.Status;

import java.time.LocalDate;

public class CalendarResponse {

    private String name;
    private LocalDate date;
    private Status status;
    private String description;

    public CalendarResponse() {
    }

    public CalendarResponse(String name, LocalDate date, Status status, String description) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}

package com.example.webappaccounting.model;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class ShiftNative {
    private String name;
    private LocalDate shiftDate;
    private String description;
    private boolean isDuty;

    public ShiftNative() {
    }

    public ShiftNative(String name, LocalDate shiftDate, String description) {
        this.name = name;
        this.shiftDate = shiftDate;
        this.description = description;
    }

    public ShiftNative(String name, LocalDate shiftDate, String description, boolean isDuty) {
        this.name = name;
        this.shiftDate = shiftDate;
        this.description = description;
        this.isDuty = isDuty;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDate shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String printShift() {
        LocalDate date = shiftDate;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String dateString = date.format(formatter);
        return String.format("%s %s %s %s", name, dateString, description, isDuty?" - дежурный": "");
    }

    public boolean isDuty() {
        return isDuty;
    }

    public void setDuty(boolean duty) {
        isDuty = duty;
    }

    @Override
    public String toString() {
        return "Shift {" +
                "employee = '" + name + '\'' +
                ", date = " + shiftDate +
                ", interval = '" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ShiftNative shift = (ShiftNative) obj;
        return Objects.equals(shiftDate, shift.getShiftDate()) &&
                Objects.equals(description, shift.getDescription()) &&
                Objects.equals(name, shift.getName()) &&
                (isDuty == shift.isDuty);
    }
}

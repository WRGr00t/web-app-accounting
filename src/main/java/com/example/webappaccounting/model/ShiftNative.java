package com.example.webappaccounting.model;

import java.time.LocalDate;
import java.util.Objects;

public class ShiftNative {
    private String name;
    private LocalDate shiftDate;
    private String description;

    public ShiftNative() {
    }

    public ShiftNative(String name, LocalDate shiftDate, String description) {
        this.name = name;
        this.shiftDate = shiftDate;
        this.description = description;
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

    @Override
    public String toString() {
        return "Shift {" +
                "employee = '" + name + '\'' +
                ", date=" + shiftDate +
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
                Objects.equals(name, shift.getName());
    }
}

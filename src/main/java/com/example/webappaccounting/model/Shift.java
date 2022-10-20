package com.example.webappaccounting.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "shifts")
public class Shift implements Comparable<Shift>{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "shift_time", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime shiftDate;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String description;

    @Column(name = "employee_name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @Column(name = "shift_type", columnDefinition = "VARCHAR(255)", nullable = false)
    private String shiftType;

    public Shift() {
    }

    public Shift(LocalDateTime shiftDate, String description) {
        this.shiftDate = shiftDate;
        this.description = description;
    }

    public Shift(LocalDateTime shiftDate, String description, String name) {
        this.shiftDate = shiftDate;
        this.description = description;
        this.name = name;
    }

    public Shift(LocalDateTime shiftDate, String description, String name, String shiftType) {
        this.shiftDate = shiftDate;
        this.description = description;
        this.name = name;
        this.shiftType = shiftType;
    }

    public LocalDateTime getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(LocalDateTime shiftDate) {
        this.shiftDate = shiftDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShiftType() {
        return shiftType;
    }

    public void setShiftType(String shiftType) {
        this.shiftType = shiftType;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "date=" + shiftDate +
                ", interval = '" + description + '\'' +
                ", employee = '" + name + '\'' +
                ", shiftType = '" + shiftType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Shift shift = (Shift) obj;
        return Objects.equals(shiftDate, shift.shiftDate) &&
                Objects.equals(description, shift.description) &&
                Objects.equals(name, shift.name);
    }

    @Override
    public int compareTo(Shift o) {
        int result = this.shiftDate.compareTo(o.shiftDate);
        if (result == 0) {
            result = this.description.compareTo(o.description);
            if (result == 0) {
                result = this.name.compareTo(o.name);
            }
        }
        return result;
    }
}

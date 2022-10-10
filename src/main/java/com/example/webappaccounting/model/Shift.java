package com.example.webappaccounting.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "shift_time", columnDefinition = "DATETIME", nullable = false)
    private LocalDateTime shiftDate;
    @Column(columnDefinition = "VARCHAR(255)", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    public Shift() {
    }

    public Shift(LocalDateTime shiftDate, String description) {
        this.shiftDate = shiftDate;
        this.description = description;
    }

    public Shift(LocalDateTime shiftDate, String description, Employee employee) {
        this.shiftDate = shiftDate;
        this.description = description;
        this.employee = employee;
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

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "Shift{" +
                "date=" + shiftDate +
                ", interval = '" + description + '\'' +
                ", employee = '" + employee + '\'' +
                '}';
    }
}

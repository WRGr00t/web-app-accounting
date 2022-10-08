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

    /*@OneToMany(mappedBy = "shifts", cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "shift2employee",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "shift_id")})
    private Set<Employee> employees = new HashSet<>();*/

    public Shift() {
    }

    public Shift(LocalDateTime shiftDate, String description) {
        this.shiftDate = shiftDate;
        this.description = description;
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

    /*public Set<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(Set<Employee> employees) {
        this.employees = employees;
    }*/

    @Override
    public String toString() {
        return "Shift{" +
                "date=" + shiftDate +
                ", interval = '" + description + '\'' +
                '}';
    }
}

package com.example.webappaccounting.model;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "employees")
public class Employee implements Comparable<Employee>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @Column(name = "shift_type", columnDefinition = "VARCHAR(255)")
    private String shiftType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee() {
    }

    public Employee(String name, String description) {
        this.name = name;
        this.shiftType = description;

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
        return "Employee{" +
                "name='" + name + '\'' +
                ", shiftType='" + shiftType + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(name, employee.name) && Objects.equals(shiftType, employee.shiftType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, shiftType);
    }

    @Override
    public int compareTo(Employee employee) {
        int result = this.name.compareTo(employee.name);
        if (result == 0) {
            result = this.shiftType.compareTo(employee.shiftType);
        }
        return result;
    }
}

package com.example.webappaccounting.model;

import javax.persistence.*;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false)
    private String name;

    @Column(name = "shift_type", columnDefinition = "VARCHAR(255)")
    private String shiftType;

    /*@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shifts_id")
    private Set<Shift> shifts;*/

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
}

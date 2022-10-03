package com.example.webappaccounting.model;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)", nullable = false)
    String name;

    String shiftCode;
    @Column(name = "is_english_speaking", columnDefinition = "TINYINT")
    boolean isEnglishSpeaking;

    @ManyToMany(mappedBy = "shifts", fetch = FetchType.LAZY)
    private Set<Shift> shifts;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee() {
    }

    public Employee(String name, String shiftCode, boolean isEnglishSpeaking) {
        this.name = name;
        this.shiftCode = shiftCode;
        this.isEnglishSpeaking = isEnglishSpeaking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShiftCode() {
        return shiftCode;
    }

    public void setShiftCode(String shiftCode) {
        this.shiftCode = shiftCode;
    }

    public boolean isEnglishSpeaking() {
        return isEnglishSpeaking;
    }

    public void setEnglishSpeaking(boolean englishSpeaking) {
        isEnglishSpeaking = englishSpeaking;
    }
}

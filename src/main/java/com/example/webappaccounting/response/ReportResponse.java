package com.example.webappaccounting.response;

public class ReportResponse implements Comparable<ReportResponse> {
    private String name;

    private int countHours;

    private int countShifts;

    private int countShiftsWithoutDinner;

    public ReportResponse() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCountHours() {
        return countHours;
    }

    public void setCountHours(int countHours) {
        this.countHours = countHours;
    }

    public int getCountShifts() {
        return countShifts;
    }

    public void setCountShifts(int countShifts) {
        this.countShifts = countShifts;
    }

    public int getCountShiftsWithoutDinner() {
        return countShiftsWithoutDinner;
    }

    public void setCountShiftsWithoutDinner(int countShiftsWithoutDinner) {
        this.countShiftsWithoutDinner = countShiftsWithoutDinner;
    }

    @Override
    public int compareTo(ReportResponse response) {
        return name.compareTo(response.getName());
    }
}

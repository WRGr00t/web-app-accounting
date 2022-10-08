package com.example.webappaccounting;

import com.example.webappaccounting.model.Shift;

import java.util.ArrayList;

public class Record {
    private String name;
    private ArrayList<Shift> shifts;

    public Record(String name, ArrayList<Shift> dateList) {
        this.name = name;
        this.shifts = dateList;
    }

    public Record() {
        this.name = "";
        this.shifts = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Shift> getShifts() {
        return shifts;
    }

    public void setDateList(ArrayList<Shift> shifts) {
        this.shifts = shifts;
    }

    @Override
    public String toString() {
        StringBuilder dates = new StringBuilder();
        for (Shift shift : shifts) {
            dates.append(shift);
            dates.append(", ");
        }

        return "Record{" +
                "name='" + name + '\'' +
                ", shift=" + dates +
                '}';
    }
}

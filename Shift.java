// Shift.java
package com.schedule.models;

import java.time.LocalDate;
import java.util.UUID;
import com.schedule.enums.ShiftType;

public class Shift {
    private UUID id;
    private LocalDate date;
    private ShiftType type;
    private Doctor doctor;

    public Shift(LocalDate date, ShiftType type, Doctor doctor) {
        this.id = UUID.randomUUID();
        this.date = date;
        this.type = type;
        this.doctor = doctor;
    }

    // Getters and setters
    public UUID getId() { return id; }
    public LocalDate getDate() { return date; }
    public ShiftType getType() { return type; }
    public Doctor getDoctor() { return doctor; }
    public void setDoctor(Doctor doctor) { this.doctor = doctor; }
}
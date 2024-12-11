// DoctorController.java
package com.schedule.controllers;

import com.schedule.models.*;
import java.time.LocalDate;
import java.util.*;

public class DoctorController {
    private Doctor doctor;
    private Schedule schedule;

    public DoctorController(Doctor doctor, Schedule schedule) {
        this.doctor = doctor;
        this.schedule = schedule;
    }

    public boolean addPreferredDate(LocalDate date) {
        return doctor.addPreferredDate(date);
    }

    public boolean addUnavailableDate(LocalDate date) {
        return doctor.addUnavailableDate(date);
    }

    public boolean requestShiftSwap(Shift shift, Doctor targetDoctor) {
        return schedule.requestShiftSwap(doctor, targetDoctor, shift);
    }

    public List<Shift> getAssignedShifts() {
        return doctor.getAssignedShifts();
    }

    public List<LocalDate> getPreferredDates() {
        return doctor.getPreferredDates();
    }

    public List<LocalDate> getUnavailableDates() {
        return doctor.getUnavailableDates();
    }

    public Doctor getDoctor() {
        return doctor;
    }
}

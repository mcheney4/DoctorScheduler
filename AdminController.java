// AdminController.java
package com.schedule.controllers;

import com.schedule.models.*;
import com.schedule.enums.*;
import java.time.LocalDate;
import java.util.*;

public class AdminController {
    private List<Doctor> doctors;
    private Schedule schedule;
    private List<Holiday> holidays;

    public AdminController() {
        this.doctors = new ArrayList<>();
        this.holidays = new ArrayList<>();
        
        // Add sample doctors
        addDoctor("Dr. Smith", SeniorityLevel.SENIOR);
        addDoctor("Dr. Johnson", SeniorityLevel.MID_LEVEL);
        addDoctor("Dr. Williams", SeniorityLevel.JUNIOR);
        
        // Add sample holidays
        addHoliday("Christmas", LocalDate.of(2024, 12, 25));
        addHoliday("New Year's Day", LocalDate.of(2024, 1, 1));
        addHoliday("Thanksgiving", LocalDate.of(2024, 11, 28));
    }

    public void createSchedule(LocalDate startDate, LocalDate endDate) {
        SchedulingStrategy strategy = new SimpleSchedulingStrategy();
        this.schedule = new Schedule(startDate, endDate, strategy);
        schedule.generateAnnualSchedule(doctors, holidays);
    }

    public void addDoctor(String name, SeniorityLevel seniority) {
        doctors.add(new Doctor(name, seniority));
    }

    public void removeDoctor(UUID doctorId) {
        doctors.removeIf(d -> d.getId().equals(doctorId));
    }

    public void addHoliday(String name, LocalDate date) {
        holidays.add(new Holiday(name, date));
    }

    public void handleShiftSwapRequest(UUID requestId, boolean approved) {
        ShiftSwapRequest request = findShiftSwapRequest(requestId);
        if (request != null) {
            if (approved) {
                request.approve();
            } else {
                request.reject();
            }
        }
    }

    private ShiftSwapRequest findShiftSwapRequest(UUID requestId) {
        return schedule.getPendingSwaps().stream()
            .filter(request -> request.getId().equals(requestId))
            .findFirst()
            .orElse(null);
    }

    // Getters
    public List<Doctor> getDoctors() {
        return new ArrayList<>(doctors);
    }

    public List<Holiday> getHolidays() {
        return new ArrayList<>(holidays);
    }

    public Schedule getSchedule() {
        return schedule;
    }
}
// Doctor.java
package com.schedule.models;

import java.time.LocalDate;
import java.util.*;
import com.schedule.enums.SeniorityLevel;

public class Doctor {
    private UUID id;
    private String name;
    private SeniorityLevel seniority;
    private List<LocalDate> preferredDates;
    private List<LocalDate> unavailableDates;
    private Map<Integer, List<Holiday>> holidaysWorked;
    private List<Shift> assignedShifts;
    private int maxPreferences;

    public Doctor(String name, SeniorityLevel seniority) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.seniority = seniority;
        this.preferredDates = new ArrayList<>();
        this.unavailableDates = new ArrayList<>();
        this.holidaysWorked = new HashMap<>();
        this.assignedShifts = new ArrayList<>();
        this.maxPreferences = seniority.getMaxPreferences();
    }

    public boolean addPreferredDate(LocalDate date) {
        if (preferredDates.size() >= maxPreferences) {
            return false;
        }
        return preferredDates.add(date);
    }

    public boolean addUnavailableDate(LocalDate date) {
        if (unavailableDates.size() >= maxPreferences) {
            return false;
        }
        return unavailableDates.add(date);
    }

    public void addHolidayWorked(Holiday holiday) {
        int year = holiday.getDate().getYear();
        holidaysWorked.computeIfAbsent(year, k -> new ArrayList<>()).add(holiday);
    }

    public boolean hasWorkedHoliday(Holiday holiday, int lookbackYears) {
        int currentYear = LocalDate.now().getYear();
        for (int year = currentYear; year > currentYear - lookbackYears; year--) {
            List<Holiday> holidays = holidaysWorked.get(year);
            if (holidays != null && holidays.contains(holiday)) {
                return true;
            }
        }
        return false;
    }

    public void addShift(Shift shift) {
        assignedShifts.add(shift);
    }

    public void removeShift(Shift shift) {
        assignedShifts.remove(shift);
    }

    // Getters and setters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public SeniorityLevel getSeniority() { return seniority; }
    public List<LocalDate> getPreferredDates() { return new ArrayList<>(preferredDates); }
    public List<LocalDate> getUnavailableDates() { return new ArrayList<>(unavailableDates); }
    public List<Shift> getAssignedShifts() { return new ArrayList<>(assignedShifts); }
}
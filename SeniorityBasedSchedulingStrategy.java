// SeniorityBasedSchedulingStrategy.java
package com.schedule.models;

import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.schedule.enums.ShiftType;

public class SeniorityBasedSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void generateSchedule(Schedule schedule, List<Doctor> doctors, List<Holiday> holidays) {
        // Sort doctors by seniority
        List<Doctor> sortedDoctors = doctors.stream()
            .sorted(Comparator.comparing(Doctor::getSeniority))
            .collect(Collectors.toList());

        // Implement scheduling logic here
        // This is a simplified version - you'll want to add more sophisticated logic
        for (Holiday holiday : holidays) {
            Doctor assignedDoctor = findEligibleDoctor(sortedDoctors, holiday);
            if (assignedDoctor != null) {
                // Create and assign holiday shift
                Shift holidayShift = new Shift(holiday.getDate(), ShiftType.ON_CALL, assignedDoctor);
                assignedDoctor.addHolidayWorked(holiday);
            }
        }
    }

    private Doctor findEligibleDoctor(List<Doctor> doctors, Holiday holiday) {
        return doctors.stream()
            .filter(d -> !d.hasWorkedHoliday(holiday, 2) && 
                        !d.getUnavailableDates().contains(holiday.getDate()))
            .findFirst()
            .orElse(null);
    }
}
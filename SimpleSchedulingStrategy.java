// SimpleSchedulingStrategy.java
package com.schedule.models;

import java.time.LocalDate;
import java.util.*;
import com.schedule.enums.ShiftType;

public class SimpleSchedulingStrategy implements SchedulingStrategy {
    @Override
    public void generateSchedule(Schedule schedule, List<Doctor> doctors, List<Holiday> holidays) {
        // Simple round-robin assignment
        Map<LocalDate, List<Shift>> dailySchedule = new HashMap<>();
        LocalDate currentDate = schedule.getStartDate();
        int doctorIndex = 0;

        while (!currentDate.isAfter(schedule.getEndDate())) {
            Doctor onCallDoctor = doctors.get(doctorIndex % doctors.size());
            Doctor onServiceDoctor = doctors.get((doctorIndex + 1) % doctors.size());

            // Check if doctor is available
            while (onCallDoctor.getUnavailableDates().contains(currentDate)) {
                doctorIndex++;
                onCallDoctor = doctors.get(doctorIndex % doctors.size());
            }

            // Create shifts
            List<Shift> dayShifts = new ArrayList<>();
            dayShifts.add(new Shift(currentDate, ShiftType.ON_CALL, onCallDoctor));
            dayShifts.add(new Shift(currentDate, ShiftType.ON_SERVICE, onServiceDoctor));

            // Add to schedule
            dailySchedule.put(currentDate, dayShifts);

            // Update doctor's assigned shifts
            onCallDoctor.getAssignedShifts().add(dayShifts.get(0));
            onServiceDoctor.getAssignedShifts().add(dayShifts.get(1));

            // Check if it's a holiday
            final LocalDate dateForFilter = currentDate;
            Optional<Holiday> holiday = holidays.stream()
                .filter(h -> h.getDate().equals(dateForFilter))
                .findFirst();

            if (holiday.isPresent()) {
                onCallDoctor.addHolidayWorked(holiday.get());
            }

            currentDate = currentDate.plusDays(1);
            doctorIndex++;
        }

        schedule.setDailySchedule(dailySchedule);
    }
}
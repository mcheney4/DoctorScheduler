// Schedule.java
package com.schedule.models;

import com.schedule.interfaces.Observer;
import com.schedule.interfaces.Subject;
import java.time.LocalDate;
import java.util.*;

public class Schedule implements Subject {
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<LocalDate, List<Shift>> dailySchedule;
    private List<ShiftSwapRequest> pendingSwaps;
    private List<Observer> observers;
    private SchedulingStrategy schedulingStrategy;

    public Schedule(LocalDate startDate, LocalDate endDate, SchedulingStrategy strategy) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.dailySchedule = new HashMap<>();
        this.pendingSwaps = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.schedulingStrategy = strategy;
    }

    public void generateAnnualSchedule(List<Doctor> doctors, List<Holiday> holidays) {
        schedulingStrategy.generateSchedule(this, doctors, holidays);
        notifyObservers();
    }

    public boolean requestShiftSwap(Doctor requester, Doctor target, Shift shift) {
        if (!isValidSwapRequest(requester, target, shift)) {
            return false;
        }

        ShiftSwapRequest swapRequest = new ShiftSwapRequest(
            requester, target, shift, LocalDate.now());
        pendingSwaps.add(swapRequest);
        notifyObservers();
        return true;
    }

    private boolean isValidSwapRequest(Doctor requester, Doctor target, Shift shift) {
        return shift.getDoctor().equals(requester) &&
               !hasConflictingShift(target, shift.getDate());
    }

    private boolean hasConflictingShift(Doctor doctor, LocalDate date) {
        List<Shift> shifts = dailySchedule.get(date);
        return shifts != null && shifts.stream()
            .anyMatch(s -> s.getDoctor().equals(doctor));
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }

    public List<Doctor> getAllDoctors() {
        Set<Doctor> doctors = new HashSet<>();
        for (List<Shift> shifts : dailySchedule.values()) {
            for (Shift shift : shifts) {
                doctors.add(shift.getDoctor());
            }
        }
        return new ArrayList<>(doctors);
    }

    // Getters and setters
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Map<LocalDate, List<Shift>> getDailySchedule() { return dailySchedule; }
    public void setDailySchedule(Map<LocalDate, List<Shift>> dailySchedule) {
        this.dailySchedule = dailySchedule;
        notifyObservers();
    }
    public List<ShiftSwapRequest> getPendingSwaps() { return pendingSwaps; }
}
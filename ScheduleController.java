
// ScheduleController.java
package com.schedule.controllers;

import com.schedule.models.*;
import java.time.LocalDate;
import java.util.*;

public class ScheduleController {
    private Schedule schedule;
    private List<ScheduleCommand> commandHistory;
    private int currentCommandIndex;

    public ScheduleController(Schedule schedule) {
        this.schedule = schedule;
        this.commandHistory = new ArrayList<>();
        this.currentCommandIndex = -1;
    }

    public void executeCommand(ScheduleCommand command) {
        command.execute();
        
        // Remove any commands after current index if we're in middle of history
        while (commandHistory.size() > currentCommandIndex + 1) {
            commandHistory.remove(commandHistory.size() - 1);
        }
        
        commandHistory.add(command);
        currentCommandIndex++;
    }

    public void undo() {
        if (currentCommandIndex >= 0) {
            commandHistory.get(currentCommandIndex).undo();
            currentCommandIndex--;
        }
    }

    public void redo() {
        if (currentCommandIndex < commandHistory.size() - 1) {
            currentCommandIndex++;
            commandHistory.get(currentCommandIndex).execute();
        }
    }

    public List<Shift> getShiftsForDate(LocalDate date) {
        return schedule.getDailySchedule().get(date);
    }

    public List<Shift> getShiftsForDoctor(Doctor doctor) {
        return doctor.getAssignedShifts();
    }

    public List<ShiftSwapRequest> getPendingSwapRequests() {
        return schedule.getPendingSwaps();
    }
}
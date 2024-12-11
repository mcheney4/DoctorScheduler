// ScheduleCommand.java
package com.schedule.models;

public interface ScheduleCommand {
    void execute();
    void undo();
}
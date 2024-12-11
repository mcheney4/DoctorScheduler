// SchedulingStrategy.java
package com.schedule.models;

import java.util.List;

public interface SchedulingStrategy {
    void generateSchedule(Schedule schedule, List<Doctor> doctors, List<Holiday> holidays);
}
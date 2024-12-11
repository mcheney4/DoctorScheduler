// SeniorityLevel.java
package com.schedule.enums;

public enum SeniorityLevel {
    JUNIOR(5),
    MID_LEVEL(7),
    SENIOR(10);

    private final int maxPreferences;

    SeniorityLevel(int maxPreferences) {
        this.maxPreferences = maxPreferences;
    }

    public int getMaxPreferences() {
        return maxPreferences;
    }
}
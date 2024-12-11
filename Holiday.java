// Holiday.java
package com.schedule.models;

import java.time.LocalDate;
import java.util.UUID;

public class Holiday {
    private UUID id;
    private String name;
    private LocalDate date;

    public Holiday(String name, LocalDate date) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.date = date;
    }

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public LocalDate getDate() { return date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Holiday)) return false;
        Holiday holiday = (Holiday) o;
        return name.equals(holiday.name) && date.equals(holiday.date);
    }

    @Override
    public int hashCode() {
        return 31 * name.hashCode() + date.hashCode();
    }
}
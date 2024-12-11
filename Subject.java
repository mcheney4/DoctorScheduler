// Subject.java (for Observer pattern)
package com.schedule.interfaces;

public interface Subject {
    void registerObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
}
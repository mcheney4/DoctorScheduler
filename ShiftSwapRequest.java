// ShiftSwapRequest.java
package com.schedule.models;

import java.time.LocalDate;
import java.util.UUID;
import com.schedule.enums.RequestStatus;

public class ShiftSwapRequest {
private UUID id;
private Doctor requester;
private Doctor target;
private Shift originalShift;
private RequestStatus status;
private LocalDate requestDate;

public ShiftSwapRequest(Doctor requester, Doctor target, 
                      Shift originalShift, LocalDate requestDate) {
    this.id = UUID.randomUUID();
    this.requester = requester;
    this.target = target;
    this.originalShift = originalShift;
    this.status = RequestStatus.PENDING;
    this.requestDate = requestDate;
}

public void approve() {
    status = RequestStatus.APPROVED;
    // Swap the shifts
    Doctor tempDoctor = originalShift.getDoctor();
    originalShift.setDoctor(target);
    // Update both doctors' assigned shifts
    requester.getAssignedShifts().remove(originalShift);
    target.getAssignedShifts().add(originalShift);
}

public void reject() {
    status = RequestStatus.REJECTED;
}

// Getters
public UUID getId() { return id; }
public Doctor getRequester() { return requester; }
public Doctor getTarget() { return target; }
public Shift getOriginalShift() { return originalShift; }
public RequestStatus getStatus() { return status; }
public LocalDate getRequestDate() { return requestDate; }
}
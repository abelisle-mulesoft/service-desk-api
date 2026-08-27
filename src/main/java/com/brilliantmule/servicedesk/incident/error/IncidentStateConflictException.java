package com.brilliantmule.servicedesk.incident.error;

/**
 * Thrown when a requested operation cannot be completed because the incident is
 * in an incompatible lifecycle state.
 * <p>
 * For example, attempting to resolve an incident that is already {@code CLOSED}.
 */
public class IncidentStateConflictException extends RuntimeException {

    /**
     * Creates an exception describing the lifecycle conflict.
     *
     * @param message detail explaining why the operation cannot be performed
     */
    public IncidentStateConflictException(String message) {
        super(message);
    }
}
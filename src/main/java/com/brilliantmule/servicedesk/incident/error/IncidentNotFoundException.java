package com.brilliantmule.servicedesk.incident.error;

/**
 * Thrown when no incident exists for the requested identifier.
 * <p>
 * Typically raised when an operation targets an incident that is not stored
 * in the repository.
 */
public class IncidentNotFoundException extends RuntimeException {

    /**
     * Creates an exception for the given incident identifier.
     *
     * @param id the identifier that could not be found (for example, {@code INC-1001})
     */
    public IncidentNotFoundException(String id) {
        super("Incident " + id + " does not exist");
    }
}

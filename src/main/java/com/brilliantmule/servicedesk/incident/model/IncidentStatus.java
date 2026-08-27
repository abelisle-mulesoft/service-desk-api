package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Lifecycle states that track an incident from creation through resolution.
 */
@Schema(description = "Current lifecycle status of a service desk incident.")
public enum IncidentStatus {

    /** The incident has been reported and is awaiting action. */
    OPEN,

    /** The incident is actively being investigated or worked on. */
    IN_PROGRESS,

    /** The underlying issue has been fixed but the incident is not yet closed. */
    RESOLVED,

    /** The incident has been completed and no further action is required. */
    CLOSED
}

package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Priority levels that indicate the urgency and impact of an incident.
 */
@Schema(description = "Priority level assigned to a service desk incident.")
public enum IncidentPriority {

    /** Minimal impact; can be addressed during normal workflow. */
    LOW,

    /** Moderate impact; should be handled within a reasonable timeframe. */
    MEDIUM,

    /** Significant impact; requires prompt attention. */
    HIGH,

    /** Severe impact; requires immediate response. */
    CRITICAL
}

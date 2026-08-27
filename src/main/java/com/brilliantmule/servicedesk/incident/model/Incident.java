package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a service desk incident, including its metadata, assignment, and comment history.
 */
@Schema(description = "Represents a service desk incident and its current state.")
public class Incident {

    @Schema(
            description = "Unique identifier assigned to the incident.",
            example = "INC-1001"
    )
    private String id;

    @Schema(
            description = "Short summary of the issue.",
            example = "Unable to access payroll"
    )
    private String title;

    @Schema(
            description = "Detailed description of the issue.",
            example = "User receives an access denied message when opening the payroll application."
    )
    private String description;

    @Schema(
            description = "Current lifecycle status of the incident.",
            example = "OPEN"
    )
    private IncidentStatus status;

    @Schema(
            description = "Priority assigned to the incident.",
            example = "HIGH"
    )
    private IncidentPriority priority;

    @Schema(
            description = "Category used to classify the incident.",
            example = "ACCESS"
    )
    private IncidentCategory category;

    @Schema(description = "Person who reported the incident.")
    private Reporter reportedBy;

    @Schema(
            description = "Person or support team currently assigned to the incident.",
            example = "Network Support"
    )
    private String assignedTo;

    @Schema(
            description = "Date and time when the incident was created.",
            example = "2026-08-25T15:00:00Z"
    )
    private Instant createdAt;

    @Schema(
            description = "Date and time when the incident was last updated.",
            example = "2026-08-25T16:30:00Z"
    )
    private Instant updatedAt;

    @Schema(description = "Comments associated with the incident.")
    private List<IncidentComment> comments = new ArrayList<>();

    /**
     * Creates an empty incident with default field values.
     */
    public Incident() {
    }

    /**
     * Creates an incident with the given field values.
     *
     * @param id          unique identifier for the incident
     * @param title       short summary of the issue
     * @param description detailed description of the issue
     * @param status      current lifecycle state
     * @param priority    urgency and impact level
     * @param category    type of issue reported
     * @param reportedBy  contact information for the person who reported the incident
     * @param assignedTo  identifier of the person or team assigned to resolve the incident
     * @param createdAt   timestamp when the incident was created
     * @param updatedAt   timestamp when the incident was last modified
     */
    public Incident(
            String id,
            String title,
            String description,
            IncidentStatus status,
            IncidentPriority priority,
            IncidentCategory category,
            Reporter reportedBy,
            String assignedTo,
            Instant createdAt,
            Instant updatedAt) {

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.category = category;
        this.reportedBy = reportedBy;
        this.assignedTo = assignedTo;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the unique identifier for this incident.
     *
     * @return the incident identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the unique identifier for this incident.
     *
     * @param id the incident identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the short summary of the issue.
     *
     * @return the incident title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the short summary of the issue.
     *
     * @param title the incident title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the detailed description of the issue.
     *
     * @return the incident description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the detailed description of the issue.
     *
     * @param description the incident description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the current lifecycle state of this incident.
     *
     * @return the incident status
     */
    public IncidentStatus getStatus() {
        return status;
    }

    /**
     * Sets the current lifecycle state of this incident.
     *
     * @param status the incident status
     */
    public void setStatus(IncidentStatus status) {
        this.status = status;
    }

    /**
     * Returns the urgency and impact level of this incident.
     *
     * @return the incident priority
     */
    public IncidentPriority getPriority() {
        return priority;
    }

    /**
     * Sets the urgency and impact level of this incident.
     *
     * @param priority the incident priority
     */
    public void setPriority(IncidentPriority priority) {
        this.priority = priority;
    }

    /**
     * Returns the type of issue reported.
     *
     * @return the incident category
     */
    public IncidentCategory getCategory() {
        return category;
    }

    /**
     * Sets the type of issue reported.
     *
     * @param category the incident category
     */
    public void setCategory(IncidentCategory category) {
        this.category = category;
    }

    /**
     * Returns contact information for the person who reported this incident.
     *
     * @return the reporter
     */
    public Reporter getReportedBy() {
        return reportedBy;
    }

    /**
     * Sets contact information for the person who reported this incident.
     *
     * @param reportedBy the reporter
     */
    public void setReportedBy(Reporter reportedBy) {
        this.reportedBy = reportedBy;
    }

    /**
     * Returns the identifier of the person or team assigned to resolve this incident.
     *
     * @return the assignee identifier, or {@code null} if unassigned
     */
    public String getAssignedTo() {
        return assignedTo;
    }

    /**
     * Sets the identifier of the person or team assigned to resolve this incident.
     *
     * @param assignedTo the assignee identifier
     */
    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    /**
     * Returns the timestamp when this incident was created.
     *
     * @return the creation timestamp
     */
    public Instant getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the timestamp when this incident was created.
     *
     * @param createdAt the creation timestamp
     */
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Returns the timestamp when this incident was last modified.
     *
     * @return the last update timestamp
     */
    public Instant getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the timestamp when this incident was last modified.
     *
     * @param updatedAt the last update timestamp
     */
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Returns the comment history for this incident.
     *
     * @return the list of comments; never {@code null}
     */
    public List<IncidentComment> getComments() {
        return comments;
    }

    /**
     * Replaces the comment history for this incident.
     *
     * @param comments the list of comments; must not be {@code null}
     */
    public void setComments(List<IncidentComment> comments) {
        this.comments = comments;
    }
}

package com.brilliantmule.servicedesk.incident.service;

import com.brilliantmule.servicedesk.incident.api.AddIncidentCommentRequest;
import com.brilliantmule.servicedesk.incident.api.AssignIncidentRequest;
import com.brilliantmule.servicedesk.incident.api.CreateIncidentRequest;
import com.brilliantmule.servicedesk.incident.api.UpdateIncidentRequest;
import com.brilliantmule.servicedesk.incident.error.IncidentNotFoundException;
import com.brilliantmule.servicedesk.incident.model.*;
import com.brilliantmule.servicedesk.incident.repository.IncidentRepository;
import org.springframework.stereotype.Service;
import com.brilliantmule.servicedesk.incident.error.IncidentStateConflictException;
import java.time.Instant;
import java.util.List;

/**
 * Application service for service desk incident operations.
 * <p>
 * Coordinates incident operations through {@link IncidentRepository}.
 */
@Service
public class IncidentService {

    private final IncidentRepository repository;

    /**
     * Creates the service with the given repository.
     *
     * @param repository store for incident persistence
     */
    public IncidentService(IncidentRepository repository) {
        this.repository = repository;
    }

    /**
     * Returns every incident, ordered by identifier ascending.
     *
     * @return all incidents; never {@code null}
     */
    public List<Incident> getIncidents() {
        return repository.findAll();
    }

    /**
     * Returns incidents matching the supplied optional filters, ordered by identifier ascending.
     * <p>
     * A {@code null} filter value is ignored. Assignee matching is case-insensitive and
     * excludes unassigned incidents when {@code assignedTo} is provided.
     *
     * @param status     incident status to match, or {@code null} to include all statuses
     * @param priority   incident priority to match, or {@code null} to include all priorities
     * @param category   incident category to match, or {@code null} to include all categories
     * @param assignedTo assignee to match, or {@code null} to include all assignments
     * @return matching incidents; never {@code null}
     */
    public List<Incident> getIncidents(
            IncidentStatus status,
            IncidentPriority priority,
            IncidentCategory category,
            String assignedTo) {

        return repository.findAll(status, priority, category, assignedTo);
    }

    /**
     * Finds the incident with the given identifier.
     * <p>
     * Lookup is case-insensitive.
     *
     * @param id incident identifier
     * @return the matching incident, or throws an exception if no incident exists with the identifier
     * @throws IncidentNotFoundException if no incident exists with the identifier
     */
    public Incident getIncident(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IncidentNotFoundException(id));
    }

    /**
     * Creates and persists a new incident from the given request.
     * <p>
     * The service assigns the identifier, sets the status to {@link IncidentStatus#OPEN},
     * initializes timestamps, and leaves assignment and comments empty.
     *
     * @param request incident creation request
     * @return the persisted incident with server-assigned fields populated
     */
    public Incident createIncident(CreateIncidentRequest request) {
        Instant now = Instant.now();

        Incident incident = new Incident(
                repository.nextId(),
                request.title(),
                request.description(),
                IncidentStatus.OPEN,
                request.priority(),
                request.category(),
                new Reporter(
                        request.reportedBy().name(),
                        request.reportedBy().email(),
                        request.reportedBy().phone()
                ),
                null,
                now,
                now
        );

        return repository.save(incident);
    }

    /**
     * Updates and persists an existing incident from the given request.
     * <p>
     * Only non-null properties in the request are applied; all others retain their
     * current values. The identifier, status, reporter, assignment, creation
     * timestamp, and comments are not modified. The {@code updatedAt} timestamp
     * is set to the current time when the incident is saved.
     * <p>
     * Lookup is case-insensitive.
     *
     * @param id      the incident identifier (for example, {@code INC-1001})
     * @param request partial update request containing the fields to change
     * @return the updated and persisted incident
     * @throws IncidentNotFoundException if no incident exists with the identifier
     */
    public Incident updateIncident(String id, UpdateIncidentRequest request) {
        Incident incident = getIncident(id);

        if (request.title() != null) {
            incident.setTitle(request.title());
        }

        if (request.description() != null) {
            incident.setDescription(request.description());
        }

        if (request.priority() != null) {
            incident.setPriority(request.priority());
        }

        if (request.category() != null) {
            incident.setCategory(request.category());
        }

        incident.setUpdatedAt(Instant.now());

        return repository.save(incident);
    }

    /**
     * Assigns an existing incident to the person or support team named in the request.
     * <p>
     * Only the assignee is updated; all other incident properties are preserved.
     * The {@code updatedAt} timestamp is set to the current time when the incident
     * is saved.
     * <p>
     * Lookup is case-insensitive.
     *
     * @param id      the incident identifier (for example, {@code INC-1001})
     * @param request assignment request containing the assignee
     * @return the updated and persisted incident
     * @throws IncidentNotFoundException if no incident exists with the identifier
     */
    public Incident assignIncident(String id, AssignIncidentRequest request) {
        Incident incident = getIncident(id);

        incident.setAssignedTo(request.assignedTo());
        incident.setUpdatedAt(Instant.now());

        return repository.save(incident);
    }

    /**
     * Adds a comment to an existing incident.
     * <p>
     * The comment is timestamped when it is added and the incident's
     * {@code updatedAt} timestamp is advanced. All other incident properties
     * are preserved.
     *
     * @param id      the incident identifier
     * @param request comment request
     * @return the updated and persisted incident
     * @throws IncidentNotFoundException if no incident exists with the identifier
     */
    public Incident addIncidentComment(String id, AddIncidentCommentRequest request) {
        Incident incident = getIncident(id);
        Instant now = Instant.now();

        incident.getComments().add(
                new IncidentComment(
                        request.author(),
                        request.text(),
                        now
                )
        );

        incident.setUpdatedAt(now);

        return repository.save(incident);
    }

    /**
     * Resolves an existing incident.
     * <p>
     * An {@link IncidentStatus#OPEN} or {@link IncidentStatus#IN_PROGRESS} incident
     * is changed to {@link IncidentStatus#RESOLVED}, and its {@code updatedAt}
     * timestamp is set to the current time. Resolving an incident that is already
     * {@code RESOLVED} is idempotent and returns the incident unchanged. A
     * {@code CLOSED} incident cannot be resolved.
     * <p>
     * Lookup is case-insensitive.
     *
     * @param id the incident identifier (for example, {@code INC-1001})
     * @return the resolved incident
     * @throws IncidentNotFoundException      if no incident exists with the identifier
     * @throws IncidentStateConflictException if the incident is closed
     */
    public Incident resolveIncident(String id) {
        Incident incident = getIncident(id);

        if (incident.getStatus() == IncidentStatus.RESOLVED) {
            return incident;
        }

        if (incident.getStatus() == IncidentStatus.CLOSED) {
            throw new IncidentStateConflictException(
                    "Incident " + incident.getId()
                            + " cannot be resolved because it is CLOSED."
            );
        }

        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setUpdatedAt(Instant.now());

        return repository.save(incident);
    }

    /**
     * Deletes an existing incident.
     * <p>
     * Identifier lookup is case-insensitive.
     *
     * @param id the incident identifier
     * @throws IncidentNotFoundException if no incident exists with the identifier
     */
    public void deleteIncident(String id) {
        if (!repository.deleteById(id)) {
            throw new IncidentNotFoundException(id);
        }
    }
}
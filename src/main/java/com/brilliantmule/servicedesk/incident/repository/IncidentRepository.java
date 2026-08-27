package com.brilliantmule.servicedesk.incident.repository;

import com.brilliantmule.servicedesk.incident.model.Incident;
import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import com.brilliantmule.servicedesk.incident.model.IncidentStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe, in-memory store for service desk incidents.
 * <p>
 * On startup, the repository loads seed incidents from the classpath resource
 * {@value #DEFAULT_SEED_DATA_RESOURCE} and generates sequential identifiers in the
 * form {@code INC-<number>}.
 */
@Repository
public class IncidentRepository {

    private static final String DEFAULT_SEED_DATA_RESOURCE = "data/incidents.json";

    private final ConcurrentHashMap<String, Incident> incidents = new ConcurrentHashMap<>();
    private final AtomicInteger sequence = new AtomicInteger(1000);

    /**
     * Creates the repository and loads seed incidents from
     * {@value #DEFAULT_SEED_DATA_RESOURCE}.
     *
     * @param jsonMapper mapper used to deserialize seed data
     */
    @Autowired
    public IncidentRepository(JsonMapper jsonMapper) {
        this(jsonMapper, DEFAULT_SEED_DATA_RESOURCE);
    }

    /**
     * Creates the repository and loads seed incidents from the given classpath resource.
     * <p>
     * Intended for tests that need an isolated or alternate seed-data source.
     *
     * @param jsonMapper       mapper used to deserialize seed data
     * @param seedDataResource classpath location of the seed data file
     */
    IncidentRepository(JsonMapper jsonMapper, String seedDataResource) {
        loadSeedData(jsonMapper, seedDataResource);
    }

    /**
     * Returns every stored incident, ordered by identifier ascending.
     * <p>
     * Equivalent to calling the filtered {@link #findAll(IncidentStatus, IncidentPriority, IncidentCategory, String)}
     * overload with all filter parameters set to {@code null}.
     *
     * @return all incidents; never {@code null}
     */
    public List<Incident> findAll() {
        return findAll(null, null, null, null);
    }

    /**
     * Returns stored incidents matching the supplied optional filters, ordered by identifier ascending.
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
    public List<Incident> findAll(
            IncidentStatus status,
            IncidentPriority priority,
            IncidentCategory category,
            String assignedTo) {

        return incidents.values()
                .stream()
                .filter(incident -> status == null || incident.getStatus() == status)
                .filter(incident -> priority == null || incident.getPriority() == priority)
                .filter(incident -> category == null || incident.getCategory() == category)
                .filter(incident -> assignedTo == null
                        || (incident.getAssignedTo() != null
                        && incident.getAssignedTo().equalsIgnoreCase(assignedTo)))
                .sorted(Comparator.comparing(Incident::getId))
                .toList();
    }

    /**
     * Finds the incident with the given identifier.
     * <p>
     * Lookup is case-insensitive; the identifier is normalized to uppercase before matching.
     *
     * @param id the incident identifier (for example, {@code INC-1001})
     * @return the matching incident, or an empty {@code Optional} if none exists
     */
    public Optional<Incident> findById(String id) {
        return Optional.ofNullable(incidents.get(id.toUpperCase()));
    }

    /**
     * Stores the given incident, replacing any existing entry with the same identifier.
     * <p>
     * The incident is indexed by its identifier for case-insensitive lookup.
     *
     * @param incident the incident to store; must have a non-null identifier
     * @return the stored incident
     */
    public Incident save(Incident incident) {
        incidents.put(incident.getId(), incident);
        return incident;
    }

    /**
     * Generates the next sequential incident identifier.
     * <p>
     * The numeric portion continues from the highest identifier already stored,
     * including seeded incidents loaded at startup.
     *
     * @return the next identifier (for example, {@code INC-1003})
     */
    public String nextId() {
        return "INC-" + sequence.incrementAndGet();
    }

    /**
     * Stores a seeded incident and advances the identifier sequence if needed.
     * <p>
     * The numeric portion of the incident identifier is compared against the current
     * sequence so that {@link #nextId()} continues from the highest seeded value.
     *
     * @param incident the seed incident to load
     */
    private void saveInitialIncident(Incident incident) {
        incidents.put(incident.getId(), incident);

        int numericId = Integer.parseInt(incident.getId().substring(4));
        sequence.updateAndGet(current -> Math.max(current, numericId));
    }

    /**
     * Deletes the incident with the given identifier.
     * <p>
     * Lookup is case-insensitive; the identifier is normalized to uppercase before matching.
     *
     * @param id the incident identifier (for example, {@code INC-1001})
     * @return {@code true} if an incident was deleted, or {@code false} if none exists
     */
    public boolean deleteById(String id) {
        return incidents.remove(id.toUpperCase()) != null;
    }

    /**
     * Loads seed incidents from the given classpath resource into the store.
     *
     * @param jsonMapper       mapper used to deserialize seed data
     * @param seedDataResource classpath location of the JSON seed data file
     * @throws IllegalStateException if the resource is missing, unreadable, malformed, or empty
     */
    private void loadSeedData(JsonMapper jsonMapper, String seedDataResource) {
        ClassPathResource resource = new ClassPathResource(seedDataResource);

        try (InputStream inputStream = resource.getInputStream()) {
            List<Incident> seedIncidents = jsonMapper.readValue(
                    inputStream,
                    new TypeReference<List<Incident>>() {
                    }
            );

            if (seedIncidents == null || seedIncidents.isEmpty()) {
                throw new IllegalStateException(
                        "Incident seed data is empty: " + seedDataResource
                );
            }

            for (Incident incident : seedIncidents) {
                saveInitialIncident(incident);
            }
        }
        catch (IOException | JacksonException exception) {
            throw new IllegalStateException(
                    "Failed to load incident seed data from " + seedDataResource,
                    exception
            );
        }
    }
}
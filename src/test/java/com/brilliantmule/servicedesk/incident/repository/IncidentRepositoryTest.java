package com.brilliantmule.servicedesk.incident.repository;

import com.brilliantmule.servicedesk.incident.model.Incident;
import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import com.brilliantmule.servicedesk.incident.model.IncidentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link IncidentRepository}.
 */
class IncidentRepositoryTest {

    private IncidentRepository repository;
    private JsonMapper jsonMapper;

    @BeforeEach
    void setUp() {
        jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();

        repository = new IncidentRepository(jsonMapper);
    }

    @Test
    void findAllReturnsIncidentsOrderedById() {
        List<Incident> incidents = repository.findAll();

        assertFalse(incidents.isEmpty());

        for (int i = 1; i < incidents.size(); i++) {
            assertTrue(
                    incidents.get(i - 1).getId().compareTo(incidents.get(i).getId()) < 0
            );
        }
    }

    @Test
    void findAllFiltersByStatus() {
        List<Incident> incidents = repository.findAll(
                IncidentStatus.OPEN,
                null,
                null,
                null
        );

        assertFalse(incidents.isEmpty());
        assertTrue(incidents.stream()
                .allMatch(incident -> incident.getStatus() == IncidentStatus.OPEN));
    }

    @Test
    void findAllFiltersByPriority() {
        List<Incident> incidents = repository.findAll(
                null,
                IncidentPriority.MEDIUM,
                null,
                null
        );

        assertFalse(incidents.isEmpty());
        assertTrue(incidents.stream()
                .allMatch(incident -> incident.getPriority() == IncidentPriority.MEDIUM));
    }

    @Test
    void findAllFiltersByCategory() {
        List<Incident> incidents = repository.findAll(
                null,
                null,
                IncidentCategory.ACCESS,
                null
        );

        assertFalse(incidents.isEmpty());
        assertTrue(incidents.stream()
                .allMatch(incident -> incident.getCategory() == IncidentCategory.ACCESS));
    }

    @Test
    void findAllFiltersByAssignedToCaseInsensitively() {
        List<Incident> incidents = repository.findAll(
                null,
                null,
                null,
                "network support"
        );

        assertEquals(1, incidents.size());
        assertEquals("INC-1002", incidents.get(0).getId());
    }

    @Test
    void findAllCombinesFilters() {
        List<Incident> incidents = repository.findAll(
                IncidentStatus.IN_PROGRESS,
                IncidentPriority.MEDIUM,
                IncidentCategory.NETWORK,
                "Network Support"
        );

        assertEquals(1, incidents.size());
        assertEquals("INC-1002", incidents.get(0).getId());
    }

    @Test
    void findAllReturnsEmptyListWhenNothingMatches() {
        List<Incident> incidents = repository.findAll(
                null,
                null,
                null,
                "__NO_SUCH_ASSIGNEE__"
        );

        assertTrue(incidents.isEmpty());
    }

    @Test
    void findByIdIsCaseInsensitive() {
        Incident incident = repository.findById("inc-1001").orElseThrow();

        assertEquals("INC-1001", incident.getId());
        assertEquals("Unable to access payroll", incident.getTitle());
    }

    @Test
    void nextIdContinuesFromHighestSeededId() {
        List<Incident> incidents = repository.findAll();
        String highestId = incidents.get(incidents.size() - 1).getId();
        int highestNumericId = Integer.parseInt(highestId.substring(4));

        assertEquals("INC-" + (highestNumericId + 1), repository.nextId());
    }

    @Test
    void saveMakesIncidentRetrievable() {
        int initialSize = repository.findAll().size();

        Incident incident = new Incident();
        incident.setId("INC-2000");
        incident.setTitle("Test incident");

        repository.save(incident);

        Incident stored = repository.findById("inc-2000").orElseThrow();
        assertEquals("INC-2000", stored.getId());
        assertEquals("Test incident", stored.getTitle());
        assertEquals(initialSize + 1, repository.findAll().size());
    }

    @Test
    void deleteByIdRemovesIncident() {
        boolean deleted = repository.deleteById("INC-1001");

        assertTrue(deleted);
        assertTrue(repository.findById("INC-1001").isEmpty());
    }

    @Test
    void deleteByIdReturnsFalseWhenIncidentDoesNotExist() {
        boolean deleted = repository.deleteById("INC-9999");

        assertFalse(deleted);
    }

    @Test
    void constructorFailsWhenSeedDataIsMissing() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new IncidentRepository(
                        jsonMapper,
                        "data/incidents-does-not-exist.json"
                )
        );

        assertTrue(exception.getMessage()
                .contains("Failed to load incident seed data"));
    }

    @Test
    void constructorFailsWhenSeedDataIsMalformed() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new IncidentRepository(
                        jsonMapper,
                        "data/incidents-malformed.json"
                )
        );

        assertTrue(exception.getMessage()
                .contains("Failed to load incident seed data"));
    }

    @Test
    void constructorFailsWhenSeedDataIsEmpty() {
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> new IncidentRepository(
                        jsonMapper,
                        "data/incidents-empty.json"
                )
        );

        assertTrue(exception.getMessage()
                .contains("Incident seed data is empty"));
    }
}

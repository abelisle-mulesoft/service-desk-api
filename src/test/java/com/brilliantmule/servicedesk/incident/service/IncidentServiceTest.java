package com.brilliantmule.servicedesk.incident.service;

import com.brilliantmule.servicedesk.incident.api.*;
import com.brilliantmule.servicedesk.incident.error.IncidentNotFoundException;
import com.brilliantmule.servicedesk.incident.error.IncidentStateConflictException;
import com.brilliantmule.servicedesk.incident.model.Incident;
import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import com.brilliantmule.servicedesk.incident.model.IncidentStatus;
import com.brilliantmule.servicedesk.incident.repository.IncidentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link IncidentService}.
 */
@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock
    private IncidentRepository repository;

    @InjectMocks
    private IncidentService service;

    @Test
    void getIncidentThrowsWhenNotFound() {
        when(repository.findById("INC-9999")).thenReturn(Optional.empty());

        assertThrows(
                IncidentNotFoundException.class,
                () -> service.getIncident("INC-9999"));
    }

    @Test
    void createIncidentAssignsOpenStatusAndPersists() {
        CreateIncidentRequest request = new CreateIncidentRequest(
                "Unable to access payroll",
                "User receives an access denied message.",
                IncidentPriority.HIGH,
                IncidentCategory.ACCESS,
                new ReporterRequest(
                        "Jane Smith",
                        "jane.smith@brilliant-mule.com",
                        "425-555-0101"));

        when(repository.nextId()).thenReturn("INC-1003");
        when(repository.save(any(Incident.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Incident created = service.createIncident(request);

        assertEquals("INC-1003", created.getId());
        assertEquals("Unable to access payroll", created.getTitle());
        assertEquals(IncidentStatus.OPEN, created.getStatus());
        assertEquals(IncidentPriority.HIGH, created.getPriority());
        assertEquals(IncidentCategory.ACCESS, created.getCategory());
        assertEquals("Jane Smith", created.getReportedBy().name());
        assertNull(created.getAssignedTo());
        assertTrue(created.getComments().isEmpty());
        assertNotNull(created.getCreatedAt());
        assertNotNull(created.getUpdatedAt());
        assertFalse(created.getUpdatedAt().isBefore(created.getCreatedAt()));
        verify(repository).save(created);
    }

    @Test
    void updateIncidentAppliesOnlySuppliedFieldsAndPersists() {
        Incident existing = new Incident(
                "INC-1001",
                "Unable to access payroll",
                "User receives an access denied message.",
                IncidentStatus.OPEN,
                IncidentPriority.HIGH,
                IncidentCategory.ACCESS,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Jane Smith",
                        "jane.smith@brilliant-mule.com",
                        "425-555-0101"
                ),
                null,
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                java.time.Instant.parse("2026-08-25T15:00:00Z")
        );

        UpdateIncidentRequest request = new UpdateIncidentRequest(
                "Unable to access payroll application",
                null,
                IncidentPriority.CRITICAL,
                null
        );

        when(repository.findById("INC-1001"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing))
                .thenReturn(existing);

        Incident updated = service.updateIncident("INC-1001", request);

        assertEquals("INC-1001", updated.getId());
        assertEquals("Unable to access payroll application", updated.getTitle());
        assertEquals("User receives an access denied message.", updated.getDescription());
        assertEquals(IncidentStatus.OPEN, updated.getStatus());
        assertEquals(IncidentPriority.CRITICAL, updated.getPriority());
        assertEquals(IncidentCategory.ACCESS, updated.getCategory());
        assertEquals("Jane Smith", updated.getReportedBy().name());
        assertNull(updated.getAssignedTo());
        assertEquals(
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                updated.getCreatedAt()
        );
        assertTrue(updated.getUpdatedAt().isAfter(updated.getCreatedAt()));

        verify(repository).save(existing);
    }

    @Test
    void assignIncidentUpdatesAssigneeAndPersists() {
        Incident existing = new Incident(
                "INC-1001",
                "Unable to access payroll",
                "User receives an access denied message.",
                IncidentStatus.OPEN,
                IncidentPriority.HIGH,
                IncidentCategory.ACCESS,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Jane Smith",
                        "jane.smith@brilliant-mule.com",
                        "425-555-0101"
                ),
                null,
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                java.time.Instant.parse("2026-08-25T15:00:00Z")
        );

        AssignIncidentRequest request =
                new AssignIncidentRequest("Network Support");

        when(repository.findById("INC-1001"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing))
                .thenReturn(existing);

        Incident assigned = service.assignIncident("INC-1001", request);

        assertEquals("INC-1001", assigned.getId());
        assertEquals("Unable to access payroll", assigned.getTitle());
        assertEquals("User receives an access denied message.", assigned.getDescription());
        assertEquals(IncidentStatus.OPEN, assigned.getStatus());
        assertEquals(IncidentPriority.HIGH, assigned.getPriority());
        assertEquals(IncidentCategory.ACCESS, assigned.getCategory());
        assertEquals("Jane Smith", assigned.getReportedBy().name());
        assertEquals("Network Support", assigned.getAssignedTo());
        assertEquals(
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                assigned.getCreatedAt()
        );
        assertTrue(assigned.getUpdatedAt().isAfter(assigned.getCreatedAt()));

        verify(repository).save(existing);
    }

    @Test
    void addIncidentCommentAppendsCommentAndPersists() {
        Incident existing = new Incident(
                "INC-1001",
                "Unable to access payroll",
                "User receives an access denied message.",
                IncidentStatus.OPEN,
                IncidentPriority.HIGH,
                IncidentCategory.ACCESS,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Jane Smith",
                        "jane.smith@brilliant-mule.com",
                        "425-555-0101"
                ),
                null,
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                java.time.Instant.parse("2026-08-25T15:00:00Z")
        );

        AddIncidentCommentRequest request =
                new AddIncidentCommentRequest(
                        "John Davis",
                        "User confirmed the issue is still occurring."
                );

        when(repository.findById("INC-1001"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing))
                .thenReturn(existing);

        Incident updated = service.addIncidentComment("INC-1001", request);

        assertEquals(1, updated.getComments().size());
        assertEquals("John Davis", updated.getComments().get(0).author());
        assertEquals(
                "User confirmed the issue is still occurring.",
                updated.getComments().get(0).text()
        );
        assertNotNull(updated.getComments().get(0).createdAt());

        assertEquals(
                updated.getComments().get(0).createdAt(),
                updated.getUpdatedAt()
        );

        assertEquals("INC-1001", updated.getId());
        assertEquals("Unable to access payroll", updated.getTitle());
        assertEquals("User receives an access denied message.", updated.getDescription());
        assertEquals(IncidentStatus.OPEN, updated.getStatus());
        assertEquals(IncidentPriority.HIGH, updated.getPriority());
        assertEquals(IncidentCategory.ACCESS, updated.getCategory());
        assertEquals("Jane Smith", updated.getReportedBy().name());
        assertNull(updated.getAssignedTo());
        assertEquals(
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                updated.getCreatedAt()
        );

        verify(repository).save(existing);
    }

    @Test
    void resolveIncidentUpdatesStatusAndPersists() {
        Incident existing = new Incident(
                "INC-1001",
                "Unable to access payroll",
                "User receives an access denied message.",
                IncidentStatus.OPEN,
                IncidentPriority.HIGH,
                IncidentCategory.ACCESS,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Jane Smith",
                        "jane.smith@brilliant-mule.com",
                        "425-555-0101"
                ),
                null,
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                java.time.Instant.parse("2026-08-25T15:00:00Z")
        );

        when(repository.findById("INC-1001"))
                .thenReturn(Optional.of(existing));
        when(repository.save(existing))
                .thenReturn(existing);

        Incident resolved = service.resolveIncident("INC-1001");

        assertEquals("INC-1001", resolved.getId());
        assertEquals("Unable to access payroll", resolved.getTitle());
        assertEquals("User receives an access denied message.", resolved.getDescription());
        assertEquals(IncidentStatus.RESOLVED, resolved.getStatus());
        assertEquals(IncidentPriority.HIGH, resolved.getPriority());
        assertEquals(IncidentCategory.ACCESS, resolved.getCategory());
        assertEquals("Jane Smith", resolved.getReportedBy().name());
        assertNull(resolved.getAssignedTo());
        assertTrue(resolved.getComments().isEmpty());
        assertEquals(
                java.time.Instant.parse("2026-08-25T15:00:00Z"),
                resolved.getCreatedAt()
        );
        assertTrue(resolved.getUpdatedAt().isAfter(resolved.getCreatedAt()));

        verify(repository).save(existing);
    }

    @Test
    void resolveIncidentReturnsResolvedIncidentUnchanged() {
        java.time.Instant updatedAt =
                java.time.Instant.parse("2026-08-23T19:45:00Z");

        Incident existing = new Incident(
                "INC-1004",
                "Order management application fails during startup",
                "The order management desktop application closes immediately after the user signs in.",
                IncidentStatus.RESOLVED,
                IncidentPriority.CRITICAL,
                IncidentCategory.SOFTWARE,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Emily Johnson",
                        "emily.johnson@brilliant-mule.com",
                        "425-555-0104"
                ),
                "Application Support",
                java.time.Instant.parse("2026-08-22T14:10:00Z"),
                updatedAt
        );

        when(repository.findById("INC-1004"))
                .thenReturn(Optional.of(existing));

        Incident resolved = service.resolveIncident("INC-1004");

        assertSame(existing, resolved);
        assertEquals(IncidentStatus.RESOLVED, resolved.getStatus());
        assertEquals(updatedAt, resolved.getUpdatedAt());

        verify(repository).findById("INC-1004");
        verify(repository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Incident.class));
    }

    @Test
    void resolveIncidentThrowsWhenIncidentIsClosed() {
        Incident existing = new Incident(
                "INC-1014",
                "Retired application access request",
                "User requested access to an application that has been retired and is no longer available.",
                IncidentStatus.CLOSED,
                IncidentPriority.LOW,
                IncidentCategory.ACCESS,
                new com.brilliantmule.servicedesk.incident.model.Reporter(
                        "Mark Reynolds",
                        "mark.reynolds@brilliant-mule.com",
                        "425-555-0114"
                ),
                "Service Desk",
                java.time.Instant.parse("2026-08-15T16:10:00Z"),
                java.time.Instant.parse("2026-08-16T18:25:00Z")
        );

        when(repository.findById("INC-1014"))
                .thenReturn(Optional.of(existing));

        IncidentStateConflictException exception = assertThrows(
                IncidentStateConflictException.class,
                () -> service.resolveIncident("INC-1014")
        );

        assertEquals(
                "Incident INC-1014 cannot be resolved because it is CLOSED.",
                exception.getMessage()
        );

        assertEquals(IncidentStatus.CLOSED, existing.getStatus());
        assertEquals(
                java.time.Instant.parse("2026-08-16T18:25:00Z"),
                existing.getUpdatedAt()
        );

        verify(repository).findById("INC-1014");
        verify(repository, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Incident.class));
    }

    @Test
    void deleteIncidentDeletesExistingIncident() {
        when(repository.deleteById("INC-1001"))
                .thenReturn(true);

        service.deleteIncident("INC-1001");

        verify(repository).deleteById("INC-1001");
    }

    @Test
    void deleteIncidentThrowsWhenIncidentDoesNotExist() {
        when(repository.deleteById("INC-9999"))
                .thenReturn(false);

        assertThrows(
                IncidentNotFoundException.class,
                () -> service.deleteIncident("INC-9999")
        );

        verify(repository).deleteById("INC-9999");
    }
}

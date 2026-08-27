package com.brilliantmule.servicedesk.incident.api;

import com.brilliantmule.servicedesk.incident.model.Incident;
import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import com.brilliantmule.servicedesk.incident.model.IncidentStatus;
import com.brilliantmule.servicedesk.incident.service.IncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for service desk incidents.
 * <p>
 * Base path: {@code /api/incidents}
 * <p>
 * Delegates business logic to {@link IncidentService}.
 */
@Tag(
        name = "Incidents",
        description = "Operations for managing service desk incidents"
)
@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private static final String INCIDENT_EXAMPLE = """
        {
          "id": "INC-1001",
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "OPEN",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": null,
          "createdAt": "2026-08-25T15:00:00Z",
          "updatedAt": "2026-08-25T15:00:00Z",
          "comments": []
        }
        """;

    private static final String INCIDENT_LIST_EXAMPLE = """
        [
          {
            "id": "INC-1001",
            "title": "Unable to access payroll",
            "description": "User receives an access denied message when opening the payroll application.",
            "status": "OPEN",
            "priority": "HIGH",
            "category": "ACCESS",
            "reportedBy": {
              "name": "Jane Smith",
              "email": "jane.smith@brilliant-mule.com",
              "phone": "425-555-0101"
            },
            "assignedTo": null,
            "createdAt": "2026-08-25T15:00:00Z",
            "updatedAt": "2026-08-25T15:00:00Z",
            "comments": []
          },
          {
            "id": "INC-1002",
            "title": "VPN disconnects frequently",
            "description": "VPN connection drops several times during the workday.",
            "status": "IN_PROGRESS",
            "priority": "MEDIUM",
            "category": "NETWORK",
            "reportedBy": {
              "name": "Robert Chen",
              "email": "robert.chen@brilliant-mule.com",
              "phone": "425-555-0102"
            },
            "assignedTo": "Network Support",
            "createdAt": "2026-08-24T18:30:00Z",
            "updatedAt": "2026-08-25T14:15:00Z",
            "comments": []
          }
        ]
        """;

    private static final String CREATE_INCIDENT_REQUEST_EXAMPLE = """
        {
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          }
        }
        """;

    private static final String CREATED_INCIDENT_EXAMPLE = """
        {
          "id": "INC-1015",
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "OPEN",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": null,
          "createdAt": "2026-08-27T18:00:00Z",
          "updatedAt": "2026-08-27T18:00:00Z",
          "comments": []
        }
        """;

    private static final String UPDATE_INCIDENT_REQUEST_EXAMPLE = """
        {
          "title": "Unable to access payroll application",
          "priority": "CRITICAL"
        }
        """;

    private static final String ASSIGN_INCIDENT_REQUEST_EXAMPLE = """
        {
          "assignedTo": "Network Support"
        }
        """;

    private static final String ADD_INCIDENT_COMMENT_REQUEST_EXAMPLE = """
        {
          "author": "John Davis",
          "text": "User confirmed the issue is still occurring."
        }
        """;

    private static final String RESOLVED_INCIDENT_EXAMPLE = """
        {
          "id": "INC-1001",
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "RESOLVED",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": null,
          "createdAt": "2026-08-25T15:00:00Z",
          "updatedAt": "2026-08-26T19:45:00Z",
          "comments": []
        }
        """;

    private static final String UPDATED_INCIDENT_EXAMPLE = """
        {
          "id": "INC-1001",
          "title": "Unable to access payroll application",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "OPEN",
          "priority": "CRITICAL",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": null,
          "createdAt": "2026-08-25T15:00:00Z",
          "updatedAt": "2026-08-27T17:30:00Z",
          "comments": []
        }
        """;

    private static final String ASSIGNED_INCIDENT_EXAMPLE = """
        {
          "id": "INC-1001",
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "OPEN",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": "Network Support",
          "createdAt": "2026-08-25T15:00:00Z",
          "updatedAt": "2026-08-27T17:35:00Z",
          "comments": []
        }
        """;

    private static final String COMMENTED_INCIDENT_EXAMPLE = """
        {
          "id": "INC-1001",
          "title": "Unable to access payroll",
          "description": "User receives an access denied message when opening the payroll application.",
          "status": "OPEN",
          "priority": "HIGH",
          "category": "ACCESS",
          "reportedBy": {
            "name": "Jane Smith",
            "email": "jane.smith@brilliant-mule.com",
            "phone": "425-555-0101"
          },
          "assignedTo": null,
          "createdAt": "2026-08-25T15:00:00Z",
          "updatedAt": "2026-08-27T17:40:00Z",
          "comments": [
            {
              "author": "John Davis",
              "text": "User confirmed the issue is still occurring.",
              "createdAt": "2026-08-27T17:40:00Z"
            }
          ]
        }
        """;

    private static final String UPDATE_VALIDATION_ERROR_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Validation Failed",
          "status": 400,
          "detail": "The request contains invalid data.",
          "instance": "/api/incidents/INC-1001",
          "errors": [
            "title: must not be blank"
          ]
        }
        """;

    private static final String ASSIGN_VALIDATION_ERROR_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Validation Failed",
          "status": 400,
          "detail": "The request contains invalid data.",
          "instance": "/api/incidents/INC-1001/assign",
          "errors": [
            "assignedTo: must not be blank"
          ]
        }
        """;

    private static final String COMMENT_VALIDATION_ERROR_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Validation Failed",
          "status": 400,
          "detail": "The request contains invalid data.",
          "instance": "/api/incidents/INC-1001/comments",
          "errors": [
            "author: must not be blank",
            "text: must not be blank"
          ]
        }
        """;

    private static final String INVALID_QUERY_PARAMETER_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Invalid Request Parameter",
          "status": 400,
          "detail": "Invalid value 'open' for parameter 'status'.",
          "instance": "/api/incidents",
          "allowedValues": [
            "OPEN",
            "IN_PROGRESS",
            "RESOLVED",
            "CLOSED"
          ]
        }
        """;

    private static final String STATE_CONFLICT_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Invalid Incident State",
          "status": 409,
          "detail": "Incident INC-1014 cannot be resolved because it is CLOSED.",
          "instance": "/api/incidents/INC-1014/resolve"
        }
        """;

    private static final String VALIDATION_ERROR_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Validation Failed",
          "status": 400,
          "detail": "The request contains invalid data.",
          "instance": "/api/incidents",
          "errors": [
            "title: must not be blank",
            "reportedBy.email: must be a well-formed email address"
          ]
        }
        """;

    private static final String NOT_FOUND_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Incident Not Found",
          "status": 404,
          "detail": "Incident INC-9999 does not exist",
          "instance": "/api/incidents/INC-9999"
        }
        """;

    private static final String ASSIGN_NOT_FOUND_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Incident Not Found",
          "status": 404,
          "detail": "Incident INC-9999 does not exist",
          "instance": "/api/incidents/INC-9999/assign"
        }
        """;

    private static final String COMMENT_NOT_FOUND_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Incident Not Found",
          "status": 404,
          "detail": "Incident INC-9999 does not exist",
          "instance": "/api/incidents/INC-9999/comments"
        }
        """;

    private static final String RESOLVE_NOT_FOUND_EXAMPLE = """
        {
          "type": "about:blank",
          "title": "Incident Not Found",
          "status": 404,
          "detail": "Incident INC-9999 does not exist",
          "instance": "/api/incidents/INC-9999/resolve"
        }
        """;

    private final IncidentService service;

    /**
     * Creates the controller with the given service.
     *
     * @param service application service for incident operations
     */
    public IncidentController(IncidentService service) {
        this.service = service;
    }

    /**
     * Returns incidents matching the supplied optional filters, ordered by identifier ascending.
     * <p>
     * When no filters are supplied, all incidents are returned.
     *
     * @param status incident status to match, or {@code null} to include all statuses
     * @param priority incident priority to match, or {@code null} to include all priorities
     * @param category incident category to match, or {@code null} to include all categories
     * @param assignedTo assignee to match, or {@code null} to include all assignments
     * @return matching incidents; never {@code null}
     */
    @Operation(
            summary = "List incidents",
            description = "Returns service desk incidents, optionally filtered by status, priority, category, or assignee."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incidents returned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = Incident.class)
                            ),
                            examples = @ExampleObject(
                                    name = "Incident list",
                                    value = INCIDENT_LIST_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameter",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Invalid request parameter",
                                    value = INVALID_QUERY_PARAMETER_EXAMPLE
                            )
                    )
            )
    })
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Incident> getIncidents(
            @Parameter(
                    description = "Incident status to match.",
                    example = "OPEN"
            )
            @RequestParam(required = false) IncidentStatus status,

            @Parameter(
                    description = "Incident priority to match.",
                    example = "HIGH"
            )
            @RequestParam(required = false) IncidentPriority priority,

            @Parameter(
                    description = "Incident category to match.",
                    example = "ACCESS"
            )
            @RequestParam(required = false) IncidentCategory category,

            @Parameter(
                    description = "Person or support team assigned to the incident.",
                    example = "Network Support"
            )
            @RequestParam(required = false) String assignedTo) {

        return service.getIncidents(status, priority, category, assignedTo);
    }

    /**
     * Returns the incident with the given identifier.
     * <p>
     * Identifier matching is case-insensitive.
     *
     * @param id the incident identifier (for example, {@code INC-1001})
     * @return the matching incident
     */
    @Operation(
            summary = "Get an incident",
            description = "Returns a service desk incident by its identifier."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident returned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Incident",
                                    value = INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = NOT_FOUND_EXAMPLE
                            )
                    )
            )
    })
    @GetMapping(
            value = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Incident getIncident(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id) {

        return service.getIncident(id);
    }

    /**
     * Creates a new incident from the given request.
     * <p>
     * Responds with {@code 201 Created}. The server assigns the identifier, sets the
     * status to {@link IncidentStatus#OPEN}, and initializes timestamps. Assignment
     * and comments start empty.
     *
     * @param request validated incident creation request
     * @return the created incident with server-assigned fields populated
     */
    @Operation(
            summary = "Create an incident",
            description = "Creates a new service desk incident."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Incident created successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Created incident",
                                    value = CREATED_INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Validation failure",
                                    value = VALIDATION_ERROR_EXAMPLE
                            )
                    )
            )
    })
    @PostMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    public Incident createIncident(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Incident to create.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CreateIncidentRequest.class),
                            examples = @ExampleObject(
                                    name = "Create incident",
                                    value = CREATE_INCIDENT_REQUEST_EXAMPLE
                            )
                    )
            )
            @Valid @RequestBody CreateIncidentRequest request) {

        return service.createIncident(request);
    }

    /**
     * Updates an existing incident from the given request.
     * <p>
     * Responds with {@code 200 OK}. Only non-null properties in the request are
     * applied; all others retain their current values. The identifier, status,
     * reporter, assignment, comments, and creation timestamp are not modified.
     * The {@code updatedAt} timestamp is refreshed when the incident is saved.
     * <p>
     * Identifier lookup is case-insensitive.
     *
     * @param id      the incident identifier (for example, {@code INC-1001})
     * @param request validated partial update request
     * @return the updated and persisted incident
     */
    @Operation(
            summary = "Update an incident",
            description = "Updates one or more editable properties of an existing service desk incident."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident updated successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Updated incident",
                                    value = UPDATED_INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Validation failure",
                                    value = UPDATE_VALIDATION_ERROR_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = NOT_FOUND_EXAMPLE
                            )
                    )
            )
    })
    @PatchMapping(
            value = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Incident updateIncident(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Incident properties to update.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateIncidentRequest.class),
                            examples = @ExampleObject(
                                    name = "Update incident",
                                    value = UPDATE_INCIDENT_REQUEST_EXAMPLE
                            )
                    )
            )
            @Valid @RequestBody UpdateIncidentRequest request) {

        return service.updateIncident(id, request);
    }

    /**
     * Assigns an existing incident to a person or support team.
     * <p>
     * Responds with {@code 200 OK}. Only the assignment and {@code updatedAt}
     * timestamp are modified; all other incident properties are preserved.
     * <p>
     * Identifier lookup is case-insensitive.
     *
     * @param id      the incident identifier (for example, {@code INC-1001})
     * @param request validated assignment request
     * @return the updated and persisted incident
     */
    @Operation(
            summary = "Assign an incident",
            description = "Assigns an existing service desk incident to a person or support team."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident assigned successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Assigned incident",
                                    value = ASSIGNED_INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Validation failure",
                                    value = ASSIGN_VALIDATION_ERROR_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = ASSIGN_NOT_FOUND_EXAMPLE
                            )
                    )
            )
    })
    @PostMapping(
            value = "/{id}/assign",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Incident assignIncident(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Incident assignment.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AssignIncidentRequest.class),
                            examples = @ExampleObject(
                                    name = "Assign incident",
                                    value = ASSIGN_INCIDENT_REQUEST_EXAMPLE
                            )
                    )
            )
            @Valid @RequestBody AssignIncidentRequest request) {

        return service.assignIncident(id, request);
    }

    /**
     * Adds a comment to an existing incident.
     * <p>
     * Responds with {@code 200 OK}. The comment is timestamped when it is added,
     * and the incident's {@code updatedAt} timestamp is refreshed. All other
     * incident properties are preserved.
     * <p>
     * Identifier lookup is case-insensitive.
     *
     * @param id      the incident identifier (for example, {@code INC-1001})
     * @param request validated comment request
     * @return the updated and persisted incident
     */
    @Operation(
            summary = "Add an incident comment",
            description = "Adds a comment to an existing service desk incident."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Comment added successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Incident with comment",
                                    value = COMMENTED_INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Request validation failed",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Validation failure",
                                    value = COMMENT_VALIDATION_ERROR_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = COMMENT_NOT_FOUND_EXAMPLE
                            )
                    )
            )
    })
    @PostMapping(
            value = "/{id}/comments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Incident addIncidentComment(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Comment to add to the incident.",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = AddIncidentCommentRequest.class),
                            examples = @ExampleObject(
                                    name = "Add incident comment",
                                    value = ADD_INCIDENT_COMMENT_REQUEST_EXAMPLE
                            )
                    )
            )
            @Valid @RequestBody AddIncidentCommentRequest request) {

        return service.addIncidentComment(id, request);
    }

    /**
     * Resolves an existing incident.
     * <p>
     * Responds with {@code 200 OK}. An open or in-progress incident is marked as
     * {@link IncidentStatus#RESOLVED} and its {@code updatedAt} timestamp is refreshed.
     * Resolving an incident that is already resolved is idempotent and leaves it unchanged.
     * A closed incident results in {@code 409 Conflict}.
     * <p>
     * Identifier lookup is case-insensitive.
     *
     * @param id the incident identifier (for example, {@code INC-1001})
     * @return the resolved incident
     */
    @Operation(
            summary = "Resolve an incident",
            description = "Marks an existing service desk incident as resolved."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Incident resolved successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Incident.class),
                            examples = @ExampleObject(
                                    name = "Resolved incident",
                                    value = RESOLVED_INCIDENT_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = RESOLVE_NOT_FOUND_EXAMPLE
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Incident cannot be resolved because of its current state",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Invalid incident state",
                                    value = STATE_CONFLICT_EXAMPLE
                            )
                    )
            )
    })
    @PostMapping(
            value = "/{id}/resolve",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public Incident resolveIncident(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id) {

        return service.resolveIncident(id);
    }

    /**
     * Deletes an existing incident.
     *
     * @param id the incident identifier
     */
    @Operation(
            summary = "Delete an incident",
            description = "Permanently deletes an existing service desk incident."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Incident deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Incident not found",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                            schema = @Schema(implementation = ProblemDetail.class),
                            examples = @ExampleObject(
                                    name = "Incident not found",
                                    value = NOT_FOUND_EXAMPLE
                            )
                    )
            )
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteIncident(
            @Parameter(
                    description = "Unique incident identifier.",
                    example = "INC-1001"
            )
            @PathVariable String id) {

        service.deleteIncident(id);
    }
}

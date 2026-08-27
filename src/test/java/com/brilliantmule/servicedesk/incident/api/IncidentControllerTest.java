package com.brilliantmule.servicedesk.incident.api;

import com.brilliantmule.servicedesk.error.GlobalExceptionHandler;
import com.brilliantmule.servicedesk.incident.repository.IncidentRepository;
import com.brilliantmule.servicedesk.incident.service.IncidentService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the REST behavior of {@link IncidentController}.
 */
@WebMvcTest(IncidentController.class)
@Import({
        IncidentService.class,
        IncidentRepository.class,
        GlobalExceptionHandler.class
})
class IncidentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getIncidentsReturnsSeededIncidents() throws Exception {
        mockMvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id",
                        hasItems("INC-1001", "INC-1002")));
    }

    @Test
    void getIncidentsFiltersByStatus() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("status", "OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[*].status",
                        everyItem(is("OPEN"))));
    }

    @Test
    void getIncidentsFiltersByPriority() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("priority", "MEDIUM"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[*].priority",
                        everyItem(is("MEDIUM"))));
    }

    @Test
    void getIncidentsFiltersByCategory() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("category", "ACCESS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[*].category",
                        everyItem(is("ACCESS"))));
    }

    @Test
    void getIncidentsFiltersByAssignedToCaseInsensitively() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("assignedTo", "network support"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[*].assignedTo",
                        everyItem(is("Network Support"))));
    }

    @Test
    void getIncidentsCombinesFilters() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("status", "IN_PROGRESS")
                        .param("priority", "MEDIUM")
                        .param("category", "NETWORK")
                        .param("assignedTo", "Network Support"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists())
                .andExpect(jsonPath("$[*].status",
                        everyItem(is("IN_PROGRESS"))))
                .andExpect(jsonPath("$[*].priority",
                        everyItem(is("MEDIUM"))))
                .andExpect(jsonPath("$[*].category",
                        everyItem(is("NETWORK"))))
                .andExpect(jsonPath("$[*].assignedTo",
                        everyItem(is("Network Support"))));
    }

    @Test
    void getIncidentsReturnsEmptyListWhenNothingMatches() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("assignedTo", "__NO_SUCH_ASSIGNEE__"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getIncidentReturnsIncident() throws Exception {
        mockMvc.perform(get("/api/incidents/INC-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"))
                .andExpect(jsonPath("$.title").value("Unable to access payroll"));
    }

    @Test
    void getIncidentIsCaseInsensitive() throws Exception {
        mockMvc.perform(get("/api/incidents/inc-1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"));
    }

    @Test
    void getIncidentReturnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(get("/api/incidents/INC-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }

    @Test
    void getIncidentsReturnsProblemDetailForInvalidStatusParameter() throws Exception {
        mockMvc.perform(get("/api/incidents")
                        .param("status", "open"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Parameter"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("Invalid value 'open' for parameter 'status'."))
                .andExpect(jsonPath("$.allowedValues",
                        hasItems("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void updateIncidentUpdatesOnlySuppliedFields() throws Exception {
        String request = """
            {
              "title": "Unable to access payroll application",
              "priority": "CRITICAL"
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"))
                .andExpect(jsonPath("$.title")
                        .value("Unable to access payroll application"))
                .andExpect(jsonPath("$.description")
                        .value("User receives an access denied message when opening the payroll application."))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("CRITICAL"))
                .andExpect(jsonPath("$.category").value("ACCESS"))
                .andExpect(jsonPath("$.reportedBy.name").value("Jane Smith"))
                .andExpect(jsonPath("$.assignedTo").doesNotExist())
                .andExpect(jsonPath("$.createdAt").value("2026-08-25T15:00:00Z"));
    }

    @Test
    void updateIncidentReturnsNotFoundProblemDetail() throws Exception {
        String request = """
            {
              "priority": "CRITICAL"
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }

    @Test
    void updateIncidentReturnsValidationProblemDetail() throws Exception {
        String request = """
            {
              "title": ""
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("title: must not be blank")));
    }

    @Test
    void updateIncidentRejectsEmptyRequest() throws Exception {
        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("updateRequested: at least one field must be provided")));
    }

    @Test
    void updateIncidentRejectsRequestContainingOnlyNullValues() throws Exception {
        String request = """
            {
              "title": null
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("updateRequested: at least one field must be provided")));
    }

    @Test
    void updateIncidentReturnsProblemDetailForInvalidPriorityValue() throws Exception {
        String request = """
            {
              "priority": "URGENT"
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Body"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("The request body could not be read. Check the JSON syntax and supplied values."));
    }

    @Test
    void updateIncidentReturnsProblemDetailForMalformedJson() throws Exception {
        String request = """
            {
              "priority": "HIGH"
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Invalid Request Body"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail")
                        .value("The request body could not be read. Check the JSON syntax and supplied values."));
    }

    @Test
    void updateIncidentRejectsWhitespaceOnlyTitle() throws Exception {
        String request = """
            {
              "title": "   "
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("title: must not be blank")));
    }

    @Test
    void updateIncidentRejectsWhitespaceOnlyDescription() throws Exception {
        String request = """
            {
              "description": "   "
            }
            """;

        mockMvc.perform(patch("/api/incidents/INC-1001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("description: must not be blank")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void assignIncidentUpdatesAssignee() throws Exception {
        String request = """
            {
              "assignedTo": "Network Support"
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"))
                .andExpect(jsonPath("$.assignedTo").value("Network Support"))
                .andExpect(jsonPath("$.title").value("Unable to access payroll"))
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.category").value("ACCESS"))
                .andExpect(jsonPath("$.reportedBy.name").value("Jane Smith"))
                .andExpect(jsonPath("$.createdAt").value("2026-08-25T15:00:00Z"));
    }

    @Test
    void assignIncidentReturnsNotFoundProblemDetail() throws Exception {
        String request = """
            {
              "assignedTo": "Network Support"
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-9999/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }

    @Test
    void assignIncidentReturnsValidationProblemDetail() throws Exception {
        String request = """
            {
              "assignedTo": ""
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-1001/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("assignedTo: must not be blank")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void addIncidentCommentAppendsComment() throws Exception {
        String request = """
            {
              "author": "John Davis",
              "text": "User confirmed the issue is still occurring."
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-1001/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"))
                .andExpect(jsonPath("$.comments.length()").value(1))
                .andExpect(jsonPath("$.comments[0].author").value("John Davis"))
                .andExpect(jsonPath("$.comments[0].text")
                        .value("User confirmed the issue is still occurring."))
                .andExpect(jsonPath("$.comments[0].createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void addIncidentCommentReturnsNotFoundProblemDetail() throws Exception {
        String request = """
            {
              "author": "John Davis",
              "text": "Testing."
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-9999/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }

    @Test
    void addIncidentCommentReturnsValidationProblemDetail() throws Exception {
        String request = """
            {
              "author": "",
              "text": ""
            }
            """;

        mockMvc.perform(post("/api/incidents/INC-1001/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors",
                        hasItem("author: must not be blank")))
                .andExpect(jsonPath("$.errors",
                        hasItem("text: must not be blank")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void resolveIncidentUpdatesStatus() throws Exception {
        mockMvc.perform(post("/api/incidents/INC-1001/resolve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1001"))
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.title").value("Unable to access payroll"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.category").value("ACCESS"))
                .andExpect(jsonPath("$.reportedBy.name").value("Jane Smith"))
                .andExpect(jsonPath("$.createdAt").value("2026-08-25T15:00:00Z"))
                .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void resolveIncidentIsIdempotentForResolvedIncident() throws Exception {
        mockMvc.perform(post("/api/incidents/INC-1004/resolve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("INC-1004"))
                .andExpect(jsonPath("$.status").value("RESOLVED"))
                .andExpect(jsonPath("$.updatedAt")
                        .value("2026-08-23T19:45:00Z"));
    }

    @Test
    void resolveIncidentReturnsConflictForClosedIncident() throws Exception {
        mockMvc.perform(post("/api/incidents/INC-1014/resolve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Invalid Incident State"))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-1014 cannot be resolved because it is CLOSED."));
    }

    @Test
    void resolveIncidentReturnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(post("/api/incidents/INC-9999/resolve"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createIncidentReturnsCreated() throws Exception {
        String request = """
                {
                  "title": "Unable to access expense reports",
                  "description": "User receives an access denied message.",
                  "priority": "HIGH",
                  "category": "ACCESS",
                  "reportedBy": {
                    "name": "Alice Johnson",
                    "email": "alice.johnson@brilliant-mule.com",
                    "phone": "425-555-0103"
                  }
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(startsWith("INC-")))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void createIncidentCanBeRetrieved() throws Exception {
        String request = """
                {
                  "title": "Unable to access expense reports",
                  "description": "User receives an access denied message.",
                  "priority": "HIGH",
                  "category": "ACCESS",
                  "reportedBy": {
                    "name": "Alice Johnson",
                    "email": "alice.johnson@brilliant-mule.com",
                    "phone": "425-555-0103"
                  }
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andReturn();

        String id = JsonPath.read(
                createResult.getResponse().getContentAsString(),
                "$.id");

        mockMvc.perform(get("/api/incidents/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("Unable to access expense reports"))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void createIncidentReturnsValidationProblemDetail() throws Exception {
        String request = """
                {
                  "title": "",
                  "description": "Unable to access payroll",
                  "priority": "HIGH",
                  "category": "ACCESS",
                  "reportedBy": {
                    "name": "",
                    "email": "not-an-email"
                  }
                }
                """;

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation Failed"))
                .andExpect(jsonPath("$.status").value(400))
								.andExpect(jsonPath("$.errors",
												hasItem("title: must not be blank")))
								.andExpect(jsonPath("$.errors",
												hasItem("reportedBy.name: must not be blank")))
								.andExpect(jsonPath("$.errors",
												hasItem("reportedBy.email: must be a well-formed email address")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.AFTER_METHOD)
    void deleteIncidentReturnsNoContentAndRemovesIncident() throws Exception {
        mockMvc.perform(delete("/api/incidents/INC-1001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/incidents/INC-1001"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void deleteIncidentReturnsNotFoundProblemDetail() throws Exception {
        mockMvc.perform(delete("/api/incidents/INC-9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Incident Not Found"))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail")
                        .value("Incident INC-9999 does not exist"));
    }
}

package com.brilliantmule.servicedesk.incident.api;

import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for creating a new service desk incident.
 * <p>
 * Server-assigned fields such as identifier, status, assignment, timestamps,
 * and comments are not included and are set when the incident is created.
 *
 * @param title       short summary of the issue; must not be blank
 * @param description detailed description of the issue; must not be blank
 * @param priority    urgency and impact level; must not be {@code null}
 * @param category    type of issue reported; must not be {@code null}
 * @param reportedBy  contact information for the person reporting the incident;
 *                    must not be {@code null} and must pass nested validation
 */
@Schema(description = "Information required to create a new service desk incident.")
public record CreateIncidentRequest(

        @NotBlank
        @Schema(
                description = "Short summary of the issue.",
                example = "Unable to access payroll"
        )
        String title,

        @NotBlank
        @Schema(
                description = "Detailed description of the issue.",
                example = "User receives an access denied message when opening the payroll application."
        )
        String description,

        @NotNull
        @Schema(
                description = "Priority assigned to the incident.",
                example = "HIGH"
        )
        IncidentPriority priority,

        @NotNull
        @Schema(
                description = "Category used to classify the incident.",
                example = "ACCESS"
        )
        IncidentCategory category,

        @NotNull
        @Valid
        @Schema(description = "Person reporting the incident.")
        ReporterRequest reportedBy
) {
}
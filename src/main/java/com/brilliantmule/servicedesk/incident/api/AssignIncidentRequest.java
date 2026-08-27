package com.brilliantmule.servicedesk.incident.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for assigning a service desk incident to a person or support team.
 * <p>
 * Only the assignee is supplied; all other incident properties are unchanged by
 * this request.
 *
 * @param assignedTo identifier of the person or support team to assign the incident to;
 *                   must not be blank
 */
@Schema(description = "Information used to assign a service desk incident.")
public record AssignIncidentRequest(

        @NotBlank
        @Schema(
                description = "Person or support team to assign the incident to.",
                example = "Network Support"
        )
        String assignedTo
) {
}
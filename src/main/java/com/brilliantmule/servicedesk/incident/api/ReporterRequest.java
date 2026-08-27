package com.brilliantmule.servicedesk.incident.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Contact information supplied in a {@link CreateIncidentRequest} when reporting an incident.
 *
 * @param name  the reporter's full name; must not be blank
 * @param email the reporter's email address; must not be blank and must be a valid email format
 * @param phone the reporter's phone number; optional
 */
@Schema(description = "Contact information for the person reporting an incident.")
public record ReporterRequest(

        @NotBlank
        @Schema(
                description = "Full name of the person reporting the incident.",
                example = "Jane Smith"
        )
        String name,

        @NotBlank
        @Email
        @Schema(
                description = "Email address of the person reporting the incident.",
                example = "jane.smith@brilliant-mule.com"
        )
        String email,

        @Schema(
                description = "Phone number of the person reporting the incident.",
                example = "425-555-0101"
        )
        String phone
) {
}

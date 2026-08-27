package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Contact information for the person who reported an incident.
 *
 * @param name  the reporter's full name
 * @param email the reporter's email address
 * @param phone the reporter's phone number; may be {@code null}
 */
@Schema(description = "Contact information for the person who reported an incident.")
public record Reporter(

        @Schema(
                description = "Full name of the person who reported the incident.",
                example = "Jane Smith"
        )
        String name,

        @Schema(
                description = "Email address of the person who reported the incident.",
                example = "jane.smith@brilliant-mule.com"
        )
        String email,

        @Schema(
                description = "Phone number of the person who reported the incident.",
                example = "425-555-0101"
        )
        String phone
) {
}

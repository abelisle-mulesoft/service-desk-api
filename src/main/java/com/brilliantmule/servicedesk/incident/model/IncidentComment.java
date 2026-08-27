package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

/**
 * A timestamped note attached to an incident, typically added during investigation or resolution.
 *
 * @param author    the name or identifier of the person who wrote the comment
 * @param text      the body of the comment
 * @param createdAt the point in time when the comment was created; assigned by the
 *                  server when a comment is added through the API
 */
@Schema(description = "Represents a comment added to a service desk incident.")
public record IncidentComment(

        @Schema(
                description = "Name of the person who added the comment.",
                example = "John Davis"
        )
        String author,

        @Schema(
                description = "Text of the comment.",
                example = "User access has been restored and verified."
        )
        String text,

        @Schema(
                description = "Date and time when the comment was added.",
                example = "2026-08-25T17:15:00Z"
        )
        Instant createdAt
) {
}

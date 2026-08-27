package com.brilliantmule.servicedesk.incident.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for adding a comment to an existing service desk incident.
 * <p>
 * Only the comment author and text are supplied. The comment timestamp is assigned
 * by the server when the comment is added to the incident.
 *
 * @param author the name or identifier of the person adding the comment; must not be blank
 * @param text   the body of the comment; must not be blank
 */
@Schema(description = "Information used to add a comment to a service desk incident.")
public record AddIncidentCommentRequest(

        @NotBlank
        @Schema(
                description = "Name of the person adding the comment.",
                example = "John Davis"
        )
        String author,

        @NotBlank
        @Schema(
                description = "Text of the comment.",
                example = "User confirmed the issue is still occurring."
        )
        String text
) {
}
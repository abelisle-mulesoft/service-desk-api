package com.brilliantmule.servicedesk.incident.api;

import com.brilliantmule.servicedesk.incident.model.IncidentCategory;
import com.brilliantmule.servicedesk.incident.model.IncidentPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Request body for updating the editable properties of an existing service desk incident.
 * <p>
 * Server-managed fields such as identifier, status, reporter, assignment, timestamps,
 * and comments are not included and are not modified by this request.
 * <p>
 * Each field is optional; {@code null} values are ignored and the existing value is
 * retained. The request must provide at least one editable property. When provided,
 * {@code title} and {@code description} must not be blank.
 *
 * @param title       updated short summary of the issue; must not be blank when provided
 * @param description updated detailed description of the issue; must not be blank when provided
 * @param priority    updated urgency and impact level; unchanged when {@code null}
 * @param category    updated type of issue reported; unchanged when {@code null}
 */
@Schema(description = "Information used to update an existing service desk incident.")
public record UpdateIncidentRequest(

        @Pattern(
                regexp = "[\\s\\S]*\\S[\\s\\S]*",
                message = "must not be blank"
        )
        @Schema(
                description = "Updated short summary of the issue.",
                example = "Unable to access payroll application"
        )
        String title,

        @Pattern(
                regexp = "[\\s\\S]*\\S[\\s\\S]*",
                message = "must not be blank"
        )
        @Schema(
                description = "Updated detailed description of the issue.",
                example = "User receives an access denied message after authenticating to the payroll application."
        )
        String description,

        @Schema(
                description = "Updated priority assigned to the incident.",
                example = "CRITICAL"
        )
        IncidentPriority priority,

        @Schema(
                description = "Updated category used to classify the incident.",
                example = "ACCESS"
        )
        IncidentCategory category
) {

        /**
         * Indicates whether the request contains at least one property to update.
         *
         * @return {@code true} when at least one editable property is supplied
         */
        @JsonIgnore
        @Schema(hidden = true)
        @AssertTrue(message = "at least one field must be provided")
        public boolean isUpdateRequested() {
                return title != null
                        || description != null
                        || priority != null
                        || category != null;
        }
}
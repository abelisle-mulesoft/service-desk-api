package com.brilliantmule.servicedesk.incident.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Categories used to classify service desk incidents by the type of issue reported.
 */
@Schema(description = "Category used to classify a service desk incident.")
public enum IncidentCategory {

    /** Issues related to user access, permissions, or authentication. */
    ACCESS,

    /** Problems with physical devices such as laptops, monitors, or peripherals. */
    HARDWARE,

    /** Connectivity or network infrastructure issues. */
    NETWORK,

    /** Application, operating system, or other software-related issues. */
    SOFTWARE,

    /** Issues that do not fit any other category. */
    OTHER
}

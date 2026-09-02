# Brilliant Mule Service Desk API

This repository contains a simple Spring Boot REST API that simulates an IT service desk for Brilliant Mule, a fictitious company. It is primarily intended for exploring and demonstrating API management and agentic API capabilities, including exposing REST API operations as MCP tools using MuleSoft MCP Bridge.

The Brilliant Mule Service Desk API provides operations for creating, retrieving, searching, updating, assigning, commenting on, resolving, and deleting incidents. The implementation is deliberately simple and stores data in memory rather than using a persistent database.

## MuleSoft and Agentic API Exploration

The API serves as a reusable backend for exploring MuleSoft API management and agentic capabilities. Current scenarios include:

- Managing the REST API with Anypoint Omni Gateway.
- Exposing selected REST operations as MCP tools using MCP Bridge.
- Consuming the MCP tools from MCP-capable AI clients.
- Exploring MCP access, tool, and data governance policies.

## Repository Structure

`api-requests/`
: Reusable Bruno collection for exploring and testing the Brilliant Mule Service Desk API and its gateway-managed endpoints.

`openapi/`
: OpenAPI Specification for the Brilliant Mule Service Desk API (`openapi.yaml`).

`src/`
: Application source code and tests.

## Implementation Overview

The Brilliant Mule Service Desk API was implemented using a bottom-up approach—that is, starting with the Java implementation and using annotations to generate the OpenAPI Specification. The following technology stack was used:

- Java 17
- Spring Boot 4
- Maven
- OpenAPI Specification 3.0

## Getting Started

1. Clone the repository.

    ```sh
    git clone https://github.com/abelisle-mulesoft/service-desk-api.git
    ```

2. Change to the root directory of the project.

3. Optionally, compile the project.
   - On Linux or macOS:

       ```sh
       ./mvnw clean compile
       ```

   - On Windows:

       ```sh
       mvnw.cmd clean compile
       ```

4. Run the automated tests.
   - On Linux or macOS:

       ```sh
       ./mvnw test
       ```

   - On Windows:

       ```sh
       mvnw.cmd test
       ```

5. Run the project.
   - On Linux or macOS:

       ```sh
       ./mvnw spring-boot:run
       ```

   - On Windows:

       ```sh
       mvnw.cmd spring-boot:run
       ```

6. Optionally, open `api-requests/` in Bruno and use the included collection to exercise the API.

---

Copyright © 2026 Alan Belisle. Licensed under the [Apache License 2.0](LICENSE.txt).

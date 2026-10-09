# Brilliant Mule Service Desk API

This repository contains a simple Spring Boot REST API that simulates an IT service desk for Brilliant Mule, a fictitious company. It is primarily intended to explore and demonstrate API and agentic management and governance capabilities, including exposing REST API operations as MCP tools using the [MuleSoft MCP Bridge](https://www.mulesoft.com/platform/ai/model-context-protocol/mcp-bridge-tour).

The Brilliant Mule Service Desk API provides operations to create, retrieve, search, update, assign, comment on, resolve, and delete incidents. The implementation is deliberately simple and loads data in memory rather than using a persistent database.

## MuleSoft API and Agentic Exploration

The Brilliant Mule Service Desk API serves as a reusable component for exploring MuleSoft’s management and governance capabilities for APIs and agentic systems. It has been used to explore the following scenarios:

- Managing the REST API with Anypoint Omni Gateway.
- Exposing selected REST operations as MCP tools using MCP Bridge.
- Consuming the MCP tools from MCP-capable AI clients.
- Exploring MCP access, tool, and data governance policies.

Refer to [MCP Governance with MuleSoft Omni Gateway](docs/mcp-governance.md) for a summary of the MCP management and governance experiments, including key insights gained during the process.

## Repository Structure

`api-requests/`
: Reusable Bruno collection for exploring and testing the Brilliant Mule Service Desk API and its gateway-managed endpoints.

`docs/`
: Documentation and findings from MCP management and governance experiments.

`openapi/`
: OpenAPI Specification for the Brilliant Mule Service Desk API (`openapi.yaml`).

`src/`
: Application source code and tests.

## Implementation Overview

The Brilliant Mule Service Desk API was developed using a bottom-up approach, starting with the Java implementation and using annotations to generate the OpenAPI Specification. The technology stack is as follows:

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

4. Optionally, run the automated tests.
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

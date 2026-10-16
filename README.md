# Brilliant Mule Service Desk API

This repository contains a simple Spring Boot REST API that simulates an IT service desk for Brilliant Mule, a fictitious company. It was developed as a reusable reference application for exploring and demonstrating MuleSoft’s agentic management, governance, and interoperability capabilities, including Model Context Protocol (MCP), Agent-to-Agent (A2A) communication, and MuleSoft Agent Fabric.

The Brilliant Mule Service Desk API provides operations to create, retrieve, search, update, assign, comment on, resolve, and delete incidents. The implementation is deliberately simple and loads data in memory rather than using a persistent database.

## MuleSoft API and Agentic Exploration

The Brilliant Mule Service Desk API serves as a reusable component for exploring MuleSoft’s API and agentic management, governance, and interoperability capabilities. It supports experiments involving:

- Managing REST APIs with Anypoint Omni Gateway.
- Exposing REST operations as MCP tools using MuleSoft MCP Bridge.
- Exploring MCP authentication, authorization, and governance policies.
- Integrating MCP capabilities with AI clients and agents across different cloud ecosystems.
- Exploring agent discovery, management, governance, and observability with MuleSoft Agent Fabric.
- Exploring Agent-to-Agent (A2A) communication and governance.
- Implementing and comparing native MCP servers with MCP Bridge.

This repository contains the Service Desk API implementation, deployment, and REST API testing. MCP Bridge configuration, MCP management and governance policies, and related experiments are maintained separately in the [mcp-governance](https://github.com/abelisle-mulesoft/mcp-governance) repository.

## Repository Structure

`api-requests/`
: Reusable Bruno collection for exploring and testing the Brilliant Mule Service Desk API and its gateway-managed endpoints.

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

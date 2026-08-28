# API Requests

This directory contains a reusable collection of requests for exploring and testing the Brilliant Mule Service Desk API.

The collection is designed to be used with [Bruno](https://www.usebruno.com/) and uses the [OpenCollection](https://www.opencollection.com/) format. The request artifacts are stored as text files alongside the application source so they can be versioned with the API.

## Using the Collection

Open the `api-requests` directory in Bruno, select the environment you want to use, and send individual requests as needed.

Switching environments allows the same requests to be executed against different deployments without modifying the requests themselves.

## Directory Structure

```text
api-requests/
├── environments/
├── errors/
├── incidents/
├── README.md
└── opencollection.yml
```

- `environments/` — environment definitions and example environment templates.
- `incidents/` — standard incident operations and behavioral scenarios.
- `errors/` — validation and error-response scenarios.
- `opencollection.yml` — OpenCollection metadata, collection variables, and collection-level configuration.

## Environments

### Local

The `local` environment is committed to the repository and targets a locally running instance of the Service Desk API:

```text
http://localhost:8080
```

Select `local` in Bruno when running the API from the local development environment.

### Shared and Developer-Specific Environments

Environment definitions that are useful to everyone using the repository may be committed to Git.

Developer-specific environment files should remain local and be excluded from Git. These may contain endpoints or other configuration that applies only to a particular developer or deployment.

Where appropriate, the repository provides `.example.yml` environment files that document the required variables without containing developer-specific values.

For example:

```text
env-aws-dev.example.yml
```

Copy an example file to an environment-specific filename:

```bash
cp env-aws-dev.example.yml env-aws-dev.yml
```

Then update the copied file with the endpoint for your environment.

The same approach can be used for additional environments, such as Omni Gateway, test, QA, or other deployment targets.

### `baseUrl`

All requests use the `baseUrl` environment variable to identify the target Service Desk API.

For example:

```text
http://localhost:8080
```

Changing the selected environment changes `baseUrl` without requiring changes to individual requests.

## Canonical Seed Incidents

The application includes canonical seed incidents used by requests that require a known incident state or data characteristic.

| Collection variable        | Incident ID | Purpose |
| -------------------------- | ----------- | ------- |
| `openIncidentId`           | `INC-1001`  | OPEN incident used by scenarios requiring an open incident |
| `resolvedIncidentId`       | `INC-1004`  | RESOLVED incident used for resolved-state and idempotency scenarios |
| `commentHistoryIncidentId` | `INC-1013`  | Incident with a large comment history |
| `closedIncidentId`         | `INC-1014`  | CLOSED incident used for state-validation scenarios |

These identifiers are defined as collection variables in `opencollection.yml` so requests do not need to hard-code the seed incident IDs.

### Temporary Incident

Mutating requests use the `createdIncidentId` collection variable to reference a temporary incident created by the `Create incident` request.

The `Create incident` request sets `createdIncidentId` to the generated incident ID. Subsequent mutating requests use this variable instead of modifying canonical seed incidents. The `Delete incident` request removes the temporary incident when testing is complete.

## Request Categories

### Incidents

The `incidents` directory contains requests for the normal Service Desk API operations, including listing, retrieving, filtering, creating, updating, assigning, commenting on, resolving, and deleting incidents.

It also contains behavioral scenarios where a successful response depends on a particular incident state, such as resolving an already-resolved incident.

### Errors

The `errors` directory contains requests that intentionally exercise validation and error handling, such as:

- Retrieving an incident that does not exist.
- Supplying an invalid query parameter.
- Supplying an invalid enum value.
- Sending an empty update request.
- Attempting an operation that is invalid for the current incident state.

These requests make it easy to verify the API's `ProblemDetail` error responses and expected HTTP status codes.

## Repeatable Mutating Scenarios

Requests that modify incident data are designed to be repeatable against a long-running API instance without requiring the application to be restarted to restore seed data.

The mutating request sequence is:

1. Create a temporary incident and store its generated ID in `createdIncidentId`.
2. Perform update, assignment, comment, and resolution operations against the temporary incident.
3. Delete the temporary incident when testing is complete.

Canonical seed incidents are used primarily for read-only requests and scenarios that specifically require their known state or data characteristics.

This create/mutate/delete strategy keeps the collection reusable across local and remote environments and prevents repeated testing from gradually changing the seed data on a long-running server.

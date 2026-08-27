# Incident Seed Data

`incidents.json` contains the in-memory seed data loaded when the Service Desk API starts.

## Canonical Test Fixtures

The following incidents are stable baseline fixtures used by automated tests:

- `INC-1001`
- `INC-1002`

These incidents should not be removed or materially changed without updating the corresponding tests. Additional incidents may be added freely without changing the baseline fixtures.

## Test Resources

The following files are used exclusively to verify the repository's fail-fast loading behavior:

- `incidents-empty.json` contains an empty incident array.
- `incidents-malformed.json` contains deliberately malformed JSON.

These files are test fixtures and should remain invalid or empty as described above.

## Loading Behavior

The application fails to start if `incidents.json` is:

- missing or unreadable;
- malformed; or
- empty.

This behavior is intentional. Seed-data problems should be detected during application startup rather than silently producing an empty or partially initialized repository.

## Extending the Dataset

Additional incidents may be added to `incidents.json` for demos, MCP scenarios, governance testing, PII detection, payload optimization, and other experiments.

The repository determines the next generated incident identifier by finding the highest numeric portion of the seeded incident identifiers and incrementing it by one. For example, if the highest seeded identifier is `INC-1101`, the next generated identifier is `INC-1102`.

When extending the dataset:

- Preserve `INC-1001` and `INC-1002` as canonical test fixtures.
- Use unique incident identifiers.
- Use the `INC-<number>` format, where `<number>` is a positive integer between 1003 and 9999.
- Incident identifiers do not need to be contiguous.
- Treat the numeric portion of the identifier as the ordering and sequencing value.
- Ensure the file remains valid JSON that can be deserialized into the incident model.


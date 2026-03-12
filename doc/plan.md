2026-03-12T00-00-00Z-plan.v1.md
project: quiq

# quiq — Plan

## Tasks

### Task 1: Generate Kotlin/Gradle fixture ✓

Created `fixtures/merkle/` — a Merkle tree CLI app with unit tests, Cucumber acceptance tests, and report generation via `quiq --report-cuke`.

### Task 2: Cucumber report generation (Rust) ✓

Implemented `quiq --report-cuke` — reads Cucumber JSON, generates HTML report co-located alongside the input file. Styled and structured to match `doc/report.js`.

### Task 3: JUnit unit test report

Add `quiq --report-junit <file>` to generate an HTML report from JUnit XML output (produced by `./gradlew test`). Design the report style — different from the Cucumber report, focused on test class/method hierarchy rather than feature/scenario.

### Task 4: Pesticide DDT fixture and report

Add [Pesticide](https://github.com/EranAl/Pesticide) data-driven tests to `fixtures/merkle/`. Then add `quiq --report-pesticide` (or extend the JUnit report if Pesticide outputs JUnit XML).

### Task 5: TUI — unit test runner

First phase of the TUI. Listen to a running test process and display results in real time, modelled on IntelliJ's test runner panel:
- Tree view: test class → test method, with pass/fail icons
- Live update as tests complete
- Summary bar (total / passed / failed)
- Decide on the integration mechanism: JUnit Platform listener, Gradle Tooling API, or stdout parsing

### Task 6: TUI — Cucumber runner

Extend the TUI to support Cucumber test runs. Display feature → scenario → step hierarchy, updating live as scenarios complete.

### Task 7: TUI — Pesticide runner

Extend the TUI to support Pesticide / DDT test runs.

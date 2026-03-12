2026-03-12T00-00-00Z-plan.v1.md
project: quiq

# quiq — Plan

## Tasks

### Task 1: Generate Kotlin/Gradle fixture

Create `fixtures/kotlin-gradle/` — a minimal Gradle project with a handful of passing and failing tests. This will be the first target quiq runs against during development.

### Task 2: Report generation (Rust)

Implement the HTML report generator in Rust, using `doc/report.js` as a behavioural specification.

- Input: Cucumber JSON (`build/reports/cucumber/cucumber.json`)
- Output: standalone HTML file matching the style/structure of `report.js`
- Crates: `serde_json` for parsing, string templating for HTML output
- Decision: keep in Rust (not JS) to preserve the standalone binary — no Node dependency required

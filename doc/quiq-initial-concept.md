2026-03-12T00-00-00Z-cli-test-runner-tui.v1.md
project: cli-test-runner

# CLI Test Runner TUI — Initial Brainstorm

## Concept

A TUI-based test runner written in Rust. Framework-agnostic — runs tests using any testing framework, captures output, displays results in a clean noise-free interface, and generates beautiful reports.

## Core Features

- Run tests from any framework, capture output
- Real-time progress display as tests execute
- Neovim-style keybinding navigation
- Beautiful, noise-free result display
- Headless mode — can be used purely as a report generator consuming existing test output (JUnit XML, Cucumber JSON, etc.)
- Pluggable report generation — the primary ecosystem hook

## Architecture

- **Core (Rust)**: TUI, process orchestration, output capture, report generation
- **Runner plugins (Lua, possibly TS)**: framework-specific adapters that know how to invoke a test suite and parse real-time + final output
- **Pluggable from the start**: core engine in Rust, specific runners in Lua or TS for easy community extension

## Framework Priority (in order)

1. Gradle
2. Cucumber Kotlin
3. JS / TypeScript (Jest etc.)
4. Cucumber TypeScript

## Key Design Decisions

### Embedded JS vs Node dependency

- Embedding (Boa, deno_core) gives standalone binary but adds complexity/size
- Requiring Node on PATH is pragmatic — users running JS/TS test suites already have it
- Core doesn't execute user JS; it spawns processes and consumes output
- For Lua plugins: `mlua` crate embeds LuaJIT/Lua 5.4 cleanly with minimal overhead (the Neovim model)

### Real-time test output

- JUnit XML is written atomically at end of run — can't tail it mid-run
- Instead: capture stdout/stderr line-by-line, parse framework-specific progress markers
  - Gradle: `--console=plain` flag, Tooling API emits real-time test events
  - Jest: `--verbose` line output
  - Cucumber: prints scenarios as they complete
- Parse final XML/JSON report for structured results after run completes

### How IntelliJ's test runner works (reference)

- Uses TeamCity test protocol — line-based on stdout: `##teamcity[testStarted name='myTest']` etc.
- Each framework has a runner adapter translating native output into this protocol
- For Gradle specifically: uses Gradle Tooling API (JVM library) to connect to Gradle daemon and receive typed test events directly

### IDE plugins — decided against

- Neovim and IntelliJ already have test runner plugins
- Not worth building custom IDE plugins

## Headless Mode / Report Generator

- Run headless to use only the report generation capability
- Consumes standard test output formats (JUnit XML, Cucumber JSON, etc.)
- Biggest ecosystem hook: existing report generators produce poor results
- Standalone value prop — no workflow change required for adoption
- TUI runner becomes the upsell once people are using the reports

## Plugin System

- To be decided: plugins in Lua or TS (or both)
- Lua via `mlua` is the low-friction embedded option
- TS plugins would need Node on PATH

## Workflow Context

- Primary user workflow: tmux as outer window manager, nvim in one pane for code inspection, other panes for tools
- Inverted model — terminal panes rather than nvim-as-everything
- This TUI fits as a tmux pane alongside nvim, not inside it

## Open Questions

- Plugin language: Lua only, TS only, or both?
- Naming
- Report output formats (HTML? PDF? terminal-rendered?)
- What makes a "beautiful" report — needs design exploration

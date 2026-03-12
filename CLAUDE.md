# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

**quiq** is a TUI (terminal UI) application that auto-detects the project type in a repo and runs its tests, displaying results in a beautiful interface. It supports watch mode (default) and can generate an HTML report opened in the OS browser via `r`.

## Repository Structure

```
cli/          # Rust TUI binary (the quiq crate)
doc/          # Design docs and planning
example-projects/  # Sample projects used for development/testing
```

## CLI

All Rust source lives in `cli/`. Commands are via `just` from that directory:

```sh
cd cli
just        # build
just run    # run
just publish # publish to crates.io (requires cargo login first)
```

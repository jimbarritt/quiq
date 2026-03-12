default: build

build:
    cargo build --manifest-path cli/Cargo.toml

run:
    cargo run --manifest-path cli/Cargo.toml

install-local:
    cargo install --path cli

publish:
    cargo publish --manifest-path cli/Cargo.toml

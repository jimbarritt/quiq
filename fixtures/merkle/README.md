# merkle

A CLI tool for building Merkle trees, generating membership proofs, and verifying them.

## Usage

```sh
# Compute the Merkle root of items from stdin
echo -e "alice\nbob\ncarol" | ./gradlew run --args="root"

# Generate a membership proof for an item
echo -e "alice\nbob\ncarol" | ./gradlew run --args="proof --item alice"

# Verify a proof
echo -e "alice\nbob\ncarol" | ./gradlew run --args="verify --item alice --proof right:<hash>,..."
```

## Architecture

Hexagonal layout — the domain has no external dependencies:

```
domain/       Pure Merkle tree logic: MerkleHash, MerkleTree, MerkleProof
application/  Use cases: BuildTree, GenerateProof, VerifyProof
adapters/
  cli/        Parses args + stdin into domain commands
  output/     Formats domain results to stdout
```

## Development

```sh
just        # run tests (default)
just build  # build
just run root                        # compute merkle root
just run "proof --item alice"        # generate proof
just run "verify --item alice --proof right:<hash>,..."  # verify proof
```

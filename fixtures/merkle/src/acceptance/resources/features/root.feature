Feature: Compute Merkle root

  Scenario: Root of two items is a 64-character hex hash
    Given the items "alice" and "bob"
    When I compute the Merkle root
    Then the root is a 64 character hex string

  Scenario: Same items always produce the same root
    Given the items "alice" and "bob"
    When I compute the Merkle root twice
    Then both roots are equal

  Scenario: Different items produce different roots
    Given the items "alice" and "bob"
    Given the items "alice" and "carol" as second set
    When I compute both Merkle roots
    Then the roots are different

  Scenario: Odd number of items is handled
    Given the items "alice", "bob", and "carol"
    When I compute the Merkle root
    Then the root is a 64 character hex string

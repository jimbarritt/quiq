Feature: Verify membership proof

  Scenario: A valid proof verifies successfully
    Given the items "alice", "bob", "carol", and "dave"
    When I generate and verify the proof for "carol"
    Then the proof is valid

  Scenario: A proof for the wrong item fails verification
    Given the items "alice", "bob", "carol", and "dave"
    When I generate a proof for "alice" but verify it for "mallory"
    Then the proof is invalid

  Scenario: Proofs verify for all items in an odd-sized tree
    Given the items "alice", "bob", and "carol"
    When I generate and verify the proof for each item
    Then all proofs are valid

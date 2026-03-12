Feature: Generate membership proof

  Scenario: Proof is returned for an item in the tree
    Given the items "alice", "bob", "carol", and "dave"
    When I generate a proof for "alice"
    Then a proof is returned

  Scenario: No proof is returned for an item not in the tree
    Given the items "alice", "bob", "carol", and "dave"
    When I generate a proof for "eve"
    Then no proof is returned

  Scenario: Proof is returned for every item in the tree
    Given the items "alice", "bob", "carol", and "dave"
    When I generate a proof for each item
    Then a proof is returned for each item

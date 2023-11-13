Feature: One-melee scenarios

  Scenario Outline: Melees with Single Loser
    Given A single melee is started with players named "P1,P2,P3,P4"
    And "P1" has the card <p1Card>
    And "P2" has the card <p2Card>
    And "P3" has the card <p3Card>
    And "P4" has the card <p4Card>
    When all players play their first card in the melee
    Then the loser of the melee is "<loser>" and takes <injury> points of damage

  Examples:
    | p1Card | p2Card | p3Card | p4Card | loser | injury |
    | Ar13   | Ar5    | Ar12   | Ar7    | P2    | 20     |
    | Sw6    | Sw7    | Sw15   | Sw13   | P1    | 30     |
    | So11   | So12   | So6    | So5    | P4    | 40     |
    | De9    | De14   | De1    | De5    | P3    | 25     |
    | Ar13   | Ar8    | Me7    | Ar14   | P3    | 45     |
    | Ar13   | Ar8    | Me15   | Al14   | P2    | 45     |
    | Ar13   | Ar8    | Ap7    | Ar14   | P3    | 25     |
    | Ar13   | Ar8    | Ap15   | Ar14   | P2    | 25     |
    | De13   | Me14   | Me14   | Me14   | P1    | 80     |
    | De8    | Me14   | De9    | Ap10   | P1    | 45     |
    | Sw10   | Sw1    | Sw2    | Me1    | P3    | 40     |
    | Sw10   | Ap10   | Sw15   | Me10   | P3    | 40     |
    | Sw10   | Sw1    | Al2    | Me2    | P2    | 40     |
    | Al2    | De7    | Sw6    | Ar8    | P1    | 35     |
    | Al6    | Me7    | Ap8    | So5    | P4    | 45     |
    | Al12   | De7    | Sw6    | Ar8    | P3    | 35     |
    | MeSw13 | Sw10   | Sw1    | Al2    | P3    | 40     |
    | ApSw13 | Sw10   | Sw1    | Sw2    | P3    | 20     |
    | MeSw13 | Sw10   | Al10   | Ap10   | P1    | 40     |
    | ApSw13 | Sw10   | Al10   | Ap10   | P1    | 20     |
    | MeDe13 | De7    | Me14   | De10   | P2    | 70     |
    | MeDe13 | Ap7    | Me14   | De10   | P2    | 65     |
    | Sw10   | Ap10   | Sw11   | Me11   | -     | 0      |
    | Sw10   | Ap10   | Al10   | Me10   | -     | 0      |

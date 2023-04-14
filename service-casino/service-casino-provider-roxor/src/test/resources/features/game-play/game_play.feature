Feature: Roxor Game-Play
  Lets perform some roxor game-play requests

  Scenario: RGP calls lithium with a normal bet request on /rgp/game-play
    Given normal casino bet request
    When RGP places a normal bet request
    Then Lithium should have a successful response
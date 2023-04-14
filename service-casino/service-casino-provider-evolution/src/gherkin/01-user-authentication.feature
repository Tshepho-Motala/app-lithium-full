Feature: CREATETOKEN-High-level

  Scenario: Success when Account is exist
    Given a CREATETOKEN request is WELL-FORMED and VALID
    And the casino-key is valid
    And the api-token is valid

    When the request is processed
    """
    {
      "uuid": "unique request identifier",
      "player": {
        "id": "livescore_uk/123456789",
        "update": false,
        "language": "en-GB",
        "country": "UK",
        "currency": "GDP",
        "session": {
          "id": "6168fd11-7e67-4883-a828-fc33949d74bf",
          "ip": "192.168.0.1"
        },
        "group": {
          "action": "clear"
        }
      },
      "config": {
        "brand": {
          "id": "livescore_uk",
          "skin": "1"
        },
        "game": {
          "table": {
            "id": "vip-roulette-123",
            "playMode": "real_money"
          }
        },
        "channel": {
          "wrapped": false,
          "mobile": false
        }
      }
    }
    """
    And the player is retrieved

    Then a CREATETOKEN response is returned
    * the HTTP status is 200

    And the response body is:
    """
    {
      "entry": "/entry?params=c2l0ZT1fX2RlZmF1bHRfXwpnYW1lPWhvbGRlbQpBVVRIX1RPS0VOPTNlYmQ5NWY1NWEwOTQyNmRiYmFjOTcxNmNiNzEwMWE0MGMzYTlhMjA&JSESSIONID=3ebd95f55a09426dbbac9716cb7101a40c3a9a20",
      "entryEmbedded": "/entry?params=c2l0ZT1fX2RlZmF1bHRfXwpnYW1lPWhvbGRlbQpBVVRIX1RPS0VOPTNlYmQ5NWY1NWEwOTQyNmRiYmFjOTcxNmNiNzEwMWE0MGMzYTlhMjA&JSESSIONID=3ebd95f55a09426dbbac9716cb7101a40c3a9a20&embedded"
    }
    """


  Scenario: Failure when a configured property does not exist
    Given a CREATETOKEN request is WELL-FORMED
    And the casino-key is not valid
    And the api-token is not valid

    When the request is processed

    """
    {
      "uuid": "unique request identifier",
      "player": {
        "id": "livescore_uk/123456789",
        "update": false,
        "language": "en-GB",
        "country": "UK",
        "currency": "GDP",
        "session": {
          "id": "6168fd11-7e67-4883-a828-fc33949d74bf",
          "ip": "192.168.0.1"
        },
        "group": {
          "action": "clear"
        }
      },
      "config": {
        "brand": {
          "id": "livescore_uk",
          "skin": "1"
        },
        "game": {
          "table": {
            "id": "vip-roulette-123",
            "playMode": "real_money"
          }
        },
        "channel": {
          "wrapped": false,
          "mobile": false
        }
      }
    }
    """

    Then a ERROR response is returned
    And HTTP status is 
    And the response body is:
    """
    {
      "errors": [
        {
          "code": "G.0",
          "message": "Could not authenticate, please review sent data and try again. If problem persists, contact customer support"
        }
      ]
    }
    """
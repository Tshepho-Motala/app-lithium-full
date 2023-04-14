Feature: LISTGAMES-HighLevel

  # This represents a high-level specification for the LISTGAMES feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success
    Given the LISTGAMES request is WELL-FORMED and VALID
    And the domainName is livescore_uk

    When the LISTGAMES request is processed

    Then the response is returned
    And HTTP status code is 200
    And response body is:
    """
    [
      {
        "id": 0,
        "name": "Wish Upon A Jackpot",
        "domain": null,
        "providerGameId": "11594",
        "enabled": false,
        "visible": false,
        "locked": false,
        "lockedMessage": null,
        "hasLockImage": false,
        "guid": "Iforium/11594",
        ...
        "progressiveJackpot": false,
        "networkedJackpotPool": null,
        "localJackpotPool": null
      },
      ...
    ]
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure when domainName is not valid
    Given the LISTGAMES request is WELL-FORMED
    And the domainName is invalid

    When the LISTGAMES request is processed

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 512

  # ----------------------------------------------------------------------------
  Scenario: Upstream Client Error
    Given a DEMOGAME request is WELL-FORMED and VALID

    When the request is processed
    And the upstream call returns a CLIENT_ERROR

    Then failure response is returned
    And HTTP status code is 400
    And the lithiumStatusCode 400

  # ----------------------------------------------------------------------------
  Scenario: Upstream Server Error
    Given the DEMOGAME request is WELL-FORMED and VALID

    When the DEMOGAME request is processed
    And the upstream call returns a SERVER_ERROR

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 500
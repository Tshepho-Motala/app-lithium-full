Feature: DEMOGAME-HighLevel

  # This represents a high-level specification for the DEMOGAME feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success
    Given the DEMOGAME request is WELL-FORMED and VALID
    And the domainName is livescore_uk
    And the gameId is 11588
    And the lang is en

    When the DEMOGAME request is processed

    Then the response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "data": "http://localhost:9000/service-casino-provider-iforium/#!?url=baseurl.com%26casinoid%3DS0009%26gameid%3DgameId%26languagecode
      %3Den%26playmode%3Ddemo%26channelid%3Dmobile%26devicechannel%3Ddesktop%26lobbyurl%3Dhttps%253A%252F%252Fwww.operator.com%252Flobby%26
      currencycode%3DGBP",
      "data2": null,
      "message": null,
      "status": 0,
      "successful": true
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success with optional parameters
    Given the DEMOGAME request is WELL-FORMED and VALID
    And the domainName is livescore_uk
    And the gameId is 11588
    And the lang is en
    And the os is mac

    When the DEMOGAME request is processed

    Then the response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "data": "http://localhost:9000/service-casino-provider-iforium/#!?url=baseurl.com%26casinoid%3DS0009%26gameid%3DgameId%26languagecode
      %3Den%26playmode%3Ddemo%26channelid%3Dmobile%26devicechannel%3Ddesktop%26lobbyurl%3Dhttps%253A%252F%252Fwww.operator.com%252Flobby%26
      currencycode%3DGBP",
      "data2": null,
      "message": null,
      "status": 0,
      "successful": true
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Request is not well formed
    Given the DEMOGAME request is NOT WELL-FORMED

    When the DEMOGAME request is processed

    Then failure response is returned
    And HTTP status code is 400
    And the lithiumStatusCode 400

  #------------------------------------------------------------------------------
  Scenario: Failure when domainName is not valid
    Given the DEMOGAME request is WELL-FORMED
    And the domainName is invalid

    When the DEMOGAME request is processed

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 550

  #------------------------------------------------------------------------------
  Scenario: Failure when domainName is not configured
    Given the DEMOGAME request is WELL-FORMED and VALID
    And the domainName is not configured

    When the DEMOGAME request is processed

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


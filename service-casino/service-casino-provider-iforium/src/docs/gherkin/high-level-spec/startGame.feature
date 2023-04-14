Feature: STARTGAME-HighLevel

  # This represents a high-level specification for the STARTGAME feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success
    Given the STARTGAME request is WELL-FORMED and VALID
    And the domainName is livescore_uk
    And the token is eyJhbGciOiJSUzI1NiIsIn...RgyfCkSv-QLi8lJjGHeRYZw
    And the gameId is 11588
    And the lang is en

    When the STARTGAME request is processed

    Then the response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "data": "http://localhost:9000/service-casino-provider-iforium/#!?url=localhost%26casinoid%3D123%26sessiontoken%3Dfa8166f6-fe0e-493d-
      a7ba-0f7b85cbd517-17be8705954%26gameid%3D11588%26languagecode%3Den%26playmode%3Dreal%26channelid%3Dmobile%26devicechannel%3Ddesktop
      %26lobbyurl%3Dlocalhost%26currencycode%3DGBP",
      "data2": null,
      "message": null,
      "status": 0,
      "successful": true
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success with optional parameters
    Given the STARTGAME request is WELL-FORMED and VALID
    And the token is eyJhbGciOiJSUzI1NiIsInR5cCI6I...TMJywHcIDYO2G7lFUu_ijM
    And the gameId is 11588
    And the lang is en
    And the currency is GBP
    And the os is mac
    And the machineGUID is generated-uuid
    And the tutorial is true
    And the platform is desktop

    When the STARTGAME request is processed

    Then the response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "data": "http://localhost:9000/service-casino-provider-iforium/#!?url=localhost%26casinoid%3D123%26sessiontoken%
      3D113b310a-16d3-4eac-bcf8-308c21b0bb8c-17be8706888%26gameid%3D11588%26languagecode%3Den%26playmode%3Dreal%26channelid%3Dmobile%26
      devicechannel%3Ddesktop%26lobbyurl%3Dlocalhost%26currencycode%3DGBP",
      "data2": null,
      "message": null,
      "status": 0,
      "successful": true
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Request is not well formed
    Given the STARTGAME request is NOT WELL-FORMED

    When the STARTGAME request is processed

    Then failure response is returned
    And HTTP status code is 400
    And the lithiumStatusCode 400

  #------------------------------------------------------------------------------
  Scenario: Failure when domainName is not valid
    Given the STARTGAME request is WELL-FORMED
    And the domainName is invalid

    When the STARTGAME request is processed

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 550

  #------------------------------------------------------------------------------
  Scenario: Failure when domainName is not configured
    Given the STARTGAME request is WELL-FORMED and VALID
    And the domainName is not configured

    When the STARTGAME request is processed

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 512

  #------------------------------------------------------------------------------
  Scenario: Failure when token is not valid
    Given the STARTGAME request is WELL-FORMED
    And the token is invalid

    When the STARTGAME request is processed

    Then failure response is returned
    And HTTP status code is 401
    And the lithiumStatusCode 401

  # ----------------------------------------------------------------------------
  Scenario: Upstream Client Error
    Given a STARTGAME request is WELL-FORMED and VALID

    When the request is processed
    And the upstream call returns a CLIENT_ERROR

    Then failure response is returned
    And HTTP status code is 400
    And the lithiumStatusCode 400

  # ----------------------------------------------------------------------------
  Scenario: Upstream Server Error
    Given the STARTGAME request is WELL-FORMED and VALID

    When the STARTGAME request is processed
    And the upstream call returns a SERVER_ERROR

    Then failure response is returned
    And HTTP status code is 500
    And the lithiumStatusCode 500


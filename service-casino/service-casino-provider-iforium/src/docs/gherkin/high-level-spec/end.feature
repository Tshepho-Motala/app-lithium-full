Feature: END-HighLevel

  # This represents a high-level specification for the END feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success (0)
    Given an END request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be08e",
      "Timestamp": "2020-09-14T14:30:06.2794721Z",
      "GatewaySessionToken": "cc146974-b210-482e-820b-771b01d15227",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "13245Z",
      "GameID": "11588",
      "CurrencyCode": "GBP"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "13245Z"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
     }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) with optional parameters
    Given an END request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "7b1d029a-4377-4285-b318-c3b580b668bf",
      "Timestamp": "2020-10-19T15:24:07.2794721Z",
      "GatewaySessionToken": "36e3150e-fa4f-42b3-ab9f-5fdb1bd28b5e",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "136265Z",
      "GameID": "11588",
      "ContentGameProviderID": "12",
      "CurrencyCode": "GBP",
      "JackpotContribution": 0,
      "JackpotWinnings": 0,
      "FreeGameOfferCode": "OfferCode"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "136265Z"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
     }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is expired
    Given an END request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is expired

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "5b31771a-daf8-422e-819f-d09aaa6c9b0d",
      "Timestamp": "2020-09-24T16:45:06.2794721Z",
      "GatewaySessionToken": "4e8cce0c-0099-459a-a263-bdb1aad231f1",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "167845Z",
      "GameID": "11588",
      "CurrencyCode": "GBP"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "167845Z"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
     }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is empty
    Given an END request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is empty

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "fe97ef65-37bb-45f4-937d-310e02c916c5",
      "Timestamp": "2020-05-14T13:44:06.2794721Z",
      "GatewaySessionToken": "",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "865765Z",
      "GameID": "11588",
      "CurrencyCode": "GBP"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "865765Z"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is absent
    Given an END request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is absent

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "fe97ef65-37bb-45f4-937d-310e02c916c5",
      "Timestamp": "2020-05-14T13:44:06.2794721Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "865765Z",
      "GameID": "11588",
      "CurrencyCode": "GBP"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "865765Z"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Success (0) when GameRoundID is not found
    Given an END request is WELL-FORMED
    And the specified GameRoundID does not exist

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "0c5a7de8-9984-44ea-8d65-ecebed25e249",
      "Timestamp": "2021-08-33T11:44:06.2756431Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorID",
      "GameRoundID": "notExistGameRound",
      "GameID": "11588",
      "CurrencyCode": "GBP"
    }
    """
    And the CompleteBetRoundRequest is sent to upstream call
    And the GameRoundID is not found and we guess that round was closed in the past
    """
    {
      "domainName": "livescorebetUK",
      "providerGuid": "livescorebetUK/service-casino-provider-iforium",
      "roundId": "notExistGameRound"
    }
    """

    Then an END response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 1000.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
     }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when request is not well formed
    Given an END request is NOT WELL-FORMED

    When the request is processed

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -6
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when PlatformKey is not valid
    Given an END request is WELL-FORMED
    And the specified PLATFORM_KEY is invalid

    When the request is processed

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -6,
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Upstream (-1) Client Error
    Given an END request is WELL-FORMED and VALID

    When the request is processed
    And the upstream call returns a CLIENT_ERROR

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -1
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Upstream (-1) Server Error
    Given an END request is WELL-FORMED and VALID

    When the request is processed
    And the upstream call returns a SERVER_ERROR

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -1
    }
    """

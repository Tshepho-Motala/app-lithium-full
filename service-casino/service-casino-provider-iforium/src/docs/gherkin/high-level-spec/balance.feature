Feature: BALANCE-HighLevel

  # This represents a high-level specification for the BALANCE feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success
    Given a BALANCE request is WELL-FORMED and VALID
    And the specified PLAYER exists

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2020-09-14T13:21:49.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID"
    }
    """

    Then a balance response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 10.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success with optional parameters
    Given a BALANCE request is WELL-FORMED and VALID
    And the specified PLAYER exists

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2020-09-14T13:21:49.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID",
      "GameID": "11588",
      "ContentGameProviderID": "12"
    }
    """

    Then a balance response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": 0,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 10.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Player is not valid
    Given a BALANCE request is WELL-FORMED
    And the specified PLAYER does not exist

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2020-09-14T13:21:49.9546136Z",
      "OperatorAccountID": "livescorebetUK/invalidOperatorAccountID"
    }
    """

    Then a balance response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -5,
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Domain is not valid
    Given a BALANCE request is WELL-FORMED
    And the specified DOMAIN does not exist

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2020-09-14T13:21:49.9546136Z",
      "OperatorAccountID": "invalid/TestOperatorAccountID"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -5,
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: PlatformKey is not valid
    Given a BALANCE request is WELL-FORMED
    And the specified PLATFORM_KEY is invalid

    When the request is processed
    """
    {
      "PlatformKey": "invalid",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2020-09-14T13:21:49.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -6,
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Request is not well formed
    Given a BALANCE request is NOT WELL-FORMED

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
  Scenario: Upstream Client Error
    Given a BALANCE request is WELL-FORMED and VALID

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
  Scenario: Upstream Server Error
    Given a BALANCE request is WELL-FORMED and VALID

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
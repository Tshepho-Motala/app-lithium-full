Feature: Account Transaction Credit

  #  This represents a high-level specification for the CREDIT feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success (0)
    Given an CREDIT is WELL-FORMED and VALID
    And the OperatorAccountID exist

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be08e",
        "Timestamp": "2020-09-14T14:30:06.2794721Z",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "AccountTransactionID": "d6637386-c3c4-494f-830d-92223d975f37",
        "AccountTransactionTypeID": "13998",
        "CurrencyCode": "GBP",
        "Amount": 10.00
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "externalTimestamp": 1625655664721,
        "roundFinished": false,
        "bonusTran": false,
        "bonusId": -1,
        "domainName": "livescorebetUK",
        "currencyCode": "GBP",
        "providerGuid": "livescorebetUK/service-casino-provider-iforium",
        "userGuid": "livescorebetUK/TestOperatorID",
        "transactionTiebackId": null,
        "realMoneyOnly": true,
        "allowNegativeBalanceAdjustment": false,
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_CREDIT,
                    "amount": 1000,
                    "betTransactionId": "d6637386-c3c4-494f-830d-92223d975f37",
                    "transactionIdLabelOverride": "d6637386-c3c4-494f-830d-92223d975f37"
                    "additionalReference": "13998"
                }
            }
        ]
    }
    """

    Then an CREDIT response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 1010.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "d6637386-c3c4-494f-830d-92223d975f37"
         }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) with optional parameters
    Given an CREDIT is WELL-FORMED and VALID
    And the OperatorAccountID exist

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "8bc1fc98-7d2d-496d-a793-11a54700d8d1",
        "Timestamp": "2021-07-07T10:14:30.2794721Z",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "AccountTransactionID": "e60d8ae9-14a4-4a50-a12d-49814b3eaca9",
        "AccountTransactionTypeID": "13998",
        "CurrencyCode": "GBP",
        "Amount": 10.00,
        "OptionalParameters": "optional"
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "externalTimestamp": 1625655664721,
        "roundFinished": false,
        "bonusTran": false,
        "bonusId": -1,
        "domainName": "livescorebetUK",
        "currencyCode": "GBP",
        "providerGuid": "livescorebetUK/service-casino-provider-iforium",
        "userGuid": "livescorebetUK/TestOperatorID",
        "transactionTiebackId": null,
        "realMoneyOnly": true,
        "allowNegativeBalanceAdjustment": false,
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_CREDIT,
                    "amount": 1000,
                    "betTransactionId": "e60d8ae9-14a4-4a50-a12d-49814b3eaca9",
                    "transactionIdLabelOverride": "e60d8ae9-14a4-4a50-a12d-49814b3eaca9"
                    "additionalReference": "13998"
                }
            }
        ]
    }
    """

    Then an CREDIT response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 1010.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "e60d8ae9-14a4-4a50-a12d-49814b3eaca9"
         }
    }
    """

  #----------------------------------------------------------------------------
  Scenario: Failure (-6) when request is not well formed
    Given an CREDIT request is NOT WELL-FORMED

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
    Given an CREDIT request is WELL-FORMED
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
  Scenario: Failure (-5) when Domain is not valid
    Given an CREDIT request is WELL-FORMED
    And the OperatorAccountID exists
    And the Domain does not exist

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": -5
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when AccountTransactionTypeID is 811
    Given an CREDIT request is WELL-FORMED
    And the AccountTransactionTypeID is Shadow Deposit(811)

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "d3451196-3a6b-4795-8653-1a3ds6be08e",
        "Timestamp": "2020-09-20T16:31:02.2794721Z",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "AccountTransactionID": "d43537386-c3c4-494f-830d-92223245f30",
        "AccountTransactionTypeID": "811",
        "CurrencyCode": "GBP",
        "Amount": 10.00
    }
    """

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": -6
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-1) Upstream Client Error
    Given an CREDIT request is WELL-FORMED and VALID

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
  Scenario: Failure (-1) Upstream Server Error
    Given an AWARDWINNINGS request is WELL-FORMED and VALID

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
Feature: PLACEBET-HighLevel

  # This represents a high-level specification for the PLACEBET feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success (0)
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be08e",
        "Timestamp": "2021-07-07T10:14:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f8279c",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13245Z",
        "GameRoundTransactionID": "123456Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.00,
        "StartRound": true,
        "EndRound": false
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "roundId": "13245Z",
        "externalTimestamp": 1625655664721,
        "gameGuid": "livescorebetUK/game11588Guid",
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
        "gameSessionId": "dd2e8025-3362-400c-b3fe-e54947f8279c",
        "performAccessChecks": true,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_BET,
                    "amount": 100,
                    "betTransactionId": "123456Y",
                    "transactionIdLabelOverride": "123456Y"
                }
            }
        ]
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "123456Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 1.00
            }
        }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) with optional parameters
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "ada21670-9524-4fa9-b776-6b515fd60eec",
        "Timestamp": "2021-07-07T10:15:30.2794721Z",
        "GatewaySessionToken": "5221c8d4-3e89-440e-8220-4107db4ca49a",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "23245Z",
        "GameRoundTransactionID": "223456Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.00,
        "StartRound": true,
        "EndRound": false,
        "ContentGameProviderID": "12",
        "JackpotContribution": 0,
        "FreeBetCost": 0,
        "FreeGameOfferCode": "OfferCode",
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "roundId": "23245Z",
        "externalTimestamp": 1625655724721,
        "gameGuid": "livescorebetUK/game11588Guid",
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
        "gameSessionId": "5221c8d4-3e89-440e-8220-4107db4ca49a",
        "performAccessChecks": true,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_BET,
                    "amount": 100,
                    "betTransactionId": "223456Y",
                    "transactionIdLabelOverride": "223456Y"
                }
            }
        ]
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "223456Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 1.00
            }
        }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) with zero amount
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "8a225989-34ab-46bd-bf75-55b5ce4fb9cd",
        "Timestamp": "2021-07-07T10:16:30.2794721Z",
        "GatewaySessionToken": "22a209c4-2ec0-4a8a-8a1a-94f4015fa4c5",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "33245Z",
        "GameRoundTransactionID": "323456Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 0.00,
        "StartRound": true,
        "EndRound": false
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "323456Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 0.00
            }
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Success (0) when StartRound is true and GameRound exists
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
  {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be485",
        "Timestamp": "2021-07-07T10:17:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f87896",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13245Z",
        "GameRoundTransactionID": "323457Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 2.00,
        "StartRound": true,
        "EndRound": true
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 8.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "323457Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 2.00
            }
        }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when StartRound is false and EndRound is true and GameRound is closed
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
  {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be778",
        "Timestamp": "2021-07-07T10:18:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f785412",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13245Z",
        "GameRoundTransactionID": "323458Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.50,
        "StartRound": false,
        "EndRound": true
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 6.50,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "323458Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 1.50
            }
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Success (0) when StartRound is false and EndRound is false and GameRound is closed
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
  {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be456",
        "Timestamp": "2021-07-07T10:19:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f12563",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13245Z",
        "GameRoundTransactionID": "323459Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.50,
        "StartRound": false,
        "EndRound": false
    }
    """

    Then a PLACEBET response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
        "ErrorCode": 0,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 6.50,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        },
        "Result": {
            "OperatorTransactionReference": "323459Y",
            "OperatorTransactionSplit": {
                "BonusAmount": 0.00,
                "CashAmount": 1.50
            }
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-14) when StartRound is false and GameRound does not exist
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
  {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be123",
        "Timestamp": "2021-07-07T10:20:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f8456",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13246Z",
        "GameRoundTransactionID": "323460Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.50,
        "StartRound": false,
        "EndRound": false
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -14,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 6.50,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-7) when StartRound is true and GameRound is closed
    Given a PLACEBET request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed
    """
  {
        "PlatformKey": "L100",
        "Sequence": "dec51196-3a6b-4795-8653-1a4c2a6be354",
        "Timestamp": "2021-07-07T10:20:30.2794721Z",
        "GatewaySessionToken": "dd2e8025-3362-400c-b3fe-e54947f1235",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "13245Z",
        "GameRoundTransactionID": "323461Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 1.50,
        "StartRound": true,
        "EndRound": false
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -7,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when request is not well formed
    Given a PLACEBET request is NOT WELL-FORMED

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
  Scenario: Failure (-15) when GameID is not valid
    Given a PLACEBET request is WELL-FORMED
    And the specified GameID does not exist

    When the request is processed

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -15,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when PlatformKey is not valid
    Given a PLACEBET request is WELL-FORMED
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
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
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
  Scenario: Failure (-3) when GatewaySessionToken is not valid
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is expired

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -3,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-3) when Account is not valid
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID does not exist
    And the GatewaySessionToken is not expired

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -3
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-3) when OperatorAccountID does not match session
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken does not belong to that OperatorAccountID

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -3
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-9) when CurrencyCode does not match session
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken does not belong to that CurrencyCode

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -9,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-2) when player has insufficient funds
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -2,
      "Balance": {
        "CurrencyCode": "GBP",
        "CashFunds": 10.00,
        "BonusFunds": 0.00,
        "FundsPriority": "Unknown"
      }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when amount is negative
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID
    And the amount is negative

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -6,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-12) when player monthly/weekly/daily loss limit reached
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -12,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-13) when player session length limit is reached
    Given a PLACEBET request is WELL-FORMED
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired
    And the specified GatewaySessionToken belongs to that OperatorAccountID

    When the request is processed

    Then a failure response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": -13,
        "Balance": {
            "CurrencyCode": "GBP",
            "CashFunds": 10.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

 # ----------------------------------------------------------------------------
  Scenario: Failure (-1) Upstream Client Error
    Given a PLACEBET request is WELL-FORMED and VALID

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
    Given a PLACEBET request is WELL-FORMED and VALID

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

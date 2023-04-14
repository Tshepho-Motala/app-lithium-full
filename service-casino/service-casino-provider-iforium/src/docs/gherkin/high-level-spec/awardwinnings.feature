Feature: AWARDWINNINGS-HighLevel

  # This represents a high-level specification for the AWARDWINNINGS feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success (0)
    Given an AWARDWINNINGS request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired

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
        "Amount": 10.00,
        "StartRound": false,
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
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_WIN,
                    "amount": 1000,
                    "betTransactionId": "123456Y",
                    "transactionIdLabelOverride": "123456Y"
                }
            }
        ]
    }
    """

    Then an AWARDWINNINGS response is returned
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
            "OperatorTransactionReference": "123456Y"
         }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) with optional parameters
    Given an AWARDWINNINGS request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is not expired

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
        "Amount": 10.00,
        "StartRound": false,
        "EndRound": false,
        "ContentGameProviderID": "12",
        "JackpotWinnings": 0.00,
        "FreeBetOfferCode": "OfferCode"
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
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": loginEventIdLongValue,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_WIN,
                    "amount": 1000,
                    "betTransactionId": "223456Y",
                    "transactionIdLabelOverride": "223456Y"
                }
            }
        ]
    }
    """

    Then an AWARDWINNINGS response is returned
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
            "OperatorTransactionReference": "223456Y"
       }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is expired
    Given an AWARDWINNINGS request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is expired

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "5b31771a-daf8-422e-819f-d09aaa6c9b0d",
        "Timestamp": "2021-09-24T16:45:06.2794721Z",
        "GatewaySessionToken": "4e8cce0c-0099-459a-a263-bdb1aad231f1",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "167845Z",
        "GameRoundTransactionID": "167845Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 10.00,
        "StartRound": false,
        "EndRound": false
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "roundId": "167845Z",
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
        "gameSessionId": "4e8cce0c-0099-459a-a263-bdb1aad231f1",
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": -1,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_WIN,
                    "amount": 1000,
                    "betTransactionId": "167845Y",
                    "transactionIdLabelOverride": "167845Y"
                }
            }
        ]
    }
    """

    Then an AWARDWINNINGS response is returned
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
           "OperatorTransactionReference": "167845Y"
       }
     }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is empty
    Given an AWARDWINNINGS request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is empty

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "fe97ef65-37bb-45f4-937d-310e02c916c5",
        "Timestamp": "2021-05-14T13:44:06.2794721Z",
        "GatewaySessionToken": "",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "865765Z",
        "GameRoundTransactionID": "865765Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 10.00,
        "StartRound": false,
        "EndRound": false
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "roundId": "865765Z",
        "externalTimestamp": 1625655458632,
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
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": -1,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_WIN,
                    "amount": 1000,
                    "betTransactionId": "865765Y",
                    "transactionIdLabelOverride": "865765Y"
                }
            }
        ]
    }
    """

    Then an AWARDWINNINGS response is returned
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
      }
      "Result": {
          "OperatorTransactionReference": "865765Y"
      }
   }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is absent
    Given an AWARDWINNINGS request is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is absent

    When the request is processed
    """
    {
        "PlatformKey": "L100",
        "Sequence": "fe97ef65-37bb-45f4-937d-310e02c916c5",
        "Timestamp": "2021-05-14T13:44:06.2794721Z",
        "OperatorAccountID": "livescorebetUK/TestOperatorID",
        "GameRoundID": "865765Z",
        "GameRoundTransactionID": "865765Y",
        "GameID": "11588",
        "CurrencyCode": "GBP",
        "Amount": 10.00,
        "StartRound": false,
        "EndRound": false
    }
    """
    And the BalanceAdjustmentRequest is sent to upstream call
    """
    {
        "roundId": "865765Z",
        "externalTimestamp": 1625655458632,
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
        "performAccessChecks": false,
        "persistRound": true,
        "sessionId": -1,
        "adjustmentComponentList": [
            {
                "BalanceAdjustmentComponent": {
                    "adjustmentType": EBalanceAdjustmentComponentType.CASINO_WIN,
                    "amount": 1000,
                    "betTransactionId": "865765Y",
                    "transactionIdLabelOverride": "865765Y"
                }
            }
        ]
    }
    """

    Then an AWARDWINNINGS response is returned
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
      }
      "Result": {
          "OperatorTransactionReference": "865765Y"
      }
   }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when request is not well formed
    Given an AWARDWINNINGS request is NOT WELL-FORMED

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
    Given an AWARDWINNINGS request is WELL-FORMED
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
            "CashFunds": 1000.00,
            "BonusFunds": 0.00,
            "FundsPriority": "Unknown"
        }
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when PlatformKey is not valid
    Given an AWARDWINNINGS request is WELL-FORMED
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
    Given an AWARDWINNINGS request is WELL-FORMED
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
  Scenario: Failure (-5) when Account is not valid
    Given an AWARDWINNINGS request is WELL-FORMED
    And the OperatorAccountID does not exist
    And the GatewaySessionToken is not expired

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
  Scenario: Failure (-1) Upstream Client Error
    Given an AWARDWINNINGS request is WELL-FORMED and VALID

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

Feature: alertWalletCallBakNotification

  #  This represents a high-level specification for the ALERTWALLETCALLBAKNOTIFICATION feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when Source is Gameflex
    Given an ALERTWALLETCALLBAKNOTIFICATION is WELL-FORMED and VALID
    And the OperatorAccountID exists

    When the request is processed
    """
      {
          "PlatformKey": "L100",
          "Sequence": "fd080f63-35e0-482e-97f0-e5787f8be3da",
          "Timestamp": "2020-09-14T13:21:50.2518211Z",
          "GatewaySessionToken": "73ae7a27-f81b-4cc3-8de4-502e89779ee1",
          "OperatorAccountID": "livescorebetUK/TestOperatorID",
          "Source": "Gameflex",
          "AlertActionID": "xa-82b3-11eb-8dcd-0242ac130003",
          "OperatorAlertActionReference": "null",
          "OperatorAlertReference": "null",
          "GamingRegulatorCode": "GB",
          "Type": "ResponsibleGaming",
          "Method": "Reset",
          "Data": "AlertActionData"
      }
    """

    Then an ALERTWALLETCALLBAKNOTIFICATION response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when Source is OperatorWallet
    Given an ALERTWALLETCALLBAKNOTIFICATION is WELL-FORMED and VALID
    And the OperatorAccountID exists

    When the request is processed
    """
      {
          "PlatformKey": "L100",
          "Sequence": "fd080f63-35e0-482e-97f0-e5787f8be3da",
          "Timestamp": "2020-09-14T13:21:50.2518211Z",
          "GatewaySessionToken": "73ae7a27-f81b-4cc3-8de4-502e89779ee1",
          "OperatorAccountID": "livescorebetUK/TestOperatorID",
          "Source": "OperatorWallet",
          "AlertActionID": "xa-82b3-11eb-8dcd-0242ac130003",
          "OperatorAlertActionReference": "AA1234C",
          "OperatorAlertReference": "AA1234",
          "GamingRegulatorCode": "GB",
          "Type": "ResponsibleGaming",
          "Method": "Reset",
          "Data": "AlertActionData"
      }
    """

    Then an ALERTWALLETCALLBAKNOTIFICATION response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is expired
    Given an ALERTWALLETCALLBAKNOTIFICATION is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is expired

    When the request is processed
    """
      {
          "PlatformKey": "L100",
          "Sequence": "f7397815-154f-4bae-8412-e6aa0ddfae8f",
          "Timestamp": "2020-09-16T13:21:50.2518211Z",
          "GatewaySessionToken": "a1f74eb4-9a23-4738-b723-e99bdd0d04eb",
          "OperatorAccountID": "livescorebetUK/TestOperatorID",
          "Source": "Gameflex",
          "AlertActionID": "xa-82b3-11eb-8dcd-0242ac130004",
          "OperatorAlertActionReference": "null",
          "OperatorAlertReference": "null",
          "GamingRegulatorCode": "GB",
          "Type": "ResponsibleGaming",
          "Method": "Reset",
          "Data": "AlertActionData"
      }
    """

    Then an ALERTWALLETCALLBAKNOTIFICATION response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is empty
    Given an ALERTWALLETCALLBAKNOTIFICATION is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is empty

    When the request is processed
    """
      {
          "PlatformKey": "L100",
          "Sequence": "4f75b6ee-c700-4761-ba28-eeef0909adff",
          "Timestamp": "2020-09-17T13:21:50.2518211Z",
          "GatewaySessionToken": "",
          "OperatorAccountID": "livescorebetUK/TestOperatorID",
          "Source": "Gameflex",
          "AlertActionID": "xa-82b3-11eb-8dcd-0242ac130005",
          "OperatorAlertActionReference": "null",
          "OperatorAlertReference": "null",
          "GamingRegulatorCode": "GB",
          "Type": "ResponsibleGaming",
          "Method": "Close",
          "Data": "AlertActionData"
      }
    """

    Then an ALERTWALLETCALLBAKNOTIFICATION response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Success (0) when GatewaySessionToken is absent
    Given an ALERTWALLETCALLBAKNOTIFICATION is WELL-FORMED and VALID
    And the OperatorAccountID exists
    And the GatewaySessionToken is absent

    When the request is processed
    """
      {
          "PlatformKey": "L100",
          "Sequence": "9b6b86d9-b99d-4cc1-a469-be2def186544",
          "Timestamp": "2020-09-18T13:21:50.2518211Z",
          "OperatorAccountID": "livescorebetUK/TestOperatorID",
          "Source": "Gameflex",
          "AlertActionID": "xa-82b3-11eb-8dcd-0242ac130006",
          "OperatorAlertActionReference": "null",
          "OperatorAlertReference": "null",
          "GamingRegulatorCode": "GB",
          "Type": "ResponsibleGaming",
          "Method": "Close",
          "Data": "AlertActionData"
      }
    """

    Then an ALERTWALLETCALLBAKNOTIFICATION response is returned
    And HTTP status is 200
    And the response body is:
    """
    {
      "ErrorCode": 0
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Failure (-6) when request is not well formed
    Given an ALERTWALLETCALLBAKNOTIFICATION is NOT WELL-FORMED

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
  Scenario: Failure (-1) Upstream Client Error
    Given an ALERTWALLETCALLBAKNOTIFICATION request is WELL-FORMED and VALID

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
    Given an ALERTWALLETCALLBAKNOTIFICATION request is WELL-FORMED and VALID

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
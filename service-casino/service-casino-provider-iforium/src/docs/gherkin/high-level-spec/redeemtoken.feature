Feature: REDEEMTOKEN-HighLevel

  # This represents a high-level specification for the REDEEMTOKEN feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario Outline: Success
    Given a REDEEMTOKEN request is WELL-FORMED and VALID
    And the <sessiontoken> will be of the form <sessionkey>-<token-create-hex-time>
    And the difference between the <current-time> and <token-create-time> <= 60 seconds

    When the request is processed
    """
    {
      PlatformKey: "L100",
      Sequence: "bfced060-b167-431e-bb84-023d8c31f53d",
      Timestamp: "2020-09-14T13:21:50.2518211Z",
      SessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179e5f738d8",
      IPAddress: "1.2.3.4"
    }
    """

    Then a redeemtoken response is returned
    And HTTP status code is 200
    And the <gateway-sessiontoken> is equal to <sessionkey>
    And response body is:
    """
    {
      ErrorCode: 0,
      Result: {
          OperatorAccountID: "TestOperatorAccountID",
          CurrencyCode: "GBP",
          CountryCode: "GB",
          GatewaySessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c"
      }
    }
    """
    Examples:
      | sessionkey                           | current-time             | token-create-time        | token-create-hex-time | sessiontoken                                     | gateway-sessiontoken                 |
      | 20fbd3b9-eef2-49cd-9976-50f0d34e7f7c | Mon Jun 07 2021 13:14:15 | Mon Jun 07 2021 13:14:15 | 179e5f738d8           | 20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179e5f738d8 | 20fbd3b9-eef2-49cd-9976-50f0d34e7f7c |

  # ----------------------------------------------------------------------------
  Scenario: Request is not well formed
    Given a REDEEMTOKEN request is NOT WELL-FORMED

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
  Scenario: PlatformKey is not valid
    Given a REDEEMTOKEN request is WELL-FORMED
    And the PlatformKey is invalid

    When the request is processed
    """
    {
      PlatformKey: "invalid",
      Sequence: "bfced060-b167-431e-bb84-023d8c31f53d",
      Timestamp: "2020-09-14T13:21:50.2518211Z",
      SessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179b2ad1b0e",
      IPAddress: "1.2.3.4"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -6
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: SessionToken is expired
    Given a REDEEMTOKEN request is WELL-FORMED and VALID
    And the SessionToken is expired

    When the request is processed
    """
    {
      PlatformKey: "L100",
      Sequence: "bfced060-b167-431e-bb84-023d8c31f53d",
      Timestamp: "2020-09-14T13:21:50.2518211Z",
      SessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179b2ad1b0e",
      IPAddress: "1.2.3.4"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -3
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Account is not found
    Given a REDEEMTOKEN request is WELL-FORMED and VALID

    When the request is processed
    """
    {
      PlatformKey: "L100",
      Sequence: "bfced060-b167-431e-bb84-023d8c31f53d",
      Timestamp: "2020-09-14T13:21:50.2518211Z",
      SessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179b2ad1b0e",
      IPAddress: "1.2.3.4"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -3
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: SessionKey is not found
    Given a REDEEMTOKEN request is WELL-FORMED and VALID

    When the request is processed
    """
    {
      PlatformKey: "L100",
      Sequence: "bfced060-b167-431e-bb84-023d8c31f53d",
      Timestamp: "2020-09-14T13:21:50.2518211Z",
      SessionToken: "20fbd3b9-eef2-49cd-9976-50f0d34e7f7c-179b2ad1b0e",
      IPAddress: "1.2.3.4"
    }
    """

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -3
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: Upstream Client Error
    Given a REDEEMTOKEN request is WELL-FORMED and VALID

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
    Given a REDEEMTOKEN request is WELL-FORMED and VALID

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


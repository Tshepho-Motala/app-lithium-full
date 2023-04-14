Feature: CREATETOKEN-HighLevel

  # This represents a high-level specification for the CREATETOKEN feature. A detailed implementation would require detailed definitions for:
  #  # - WELL-FORMED and VALID requests
  #  # - SUCCESS and FAILURE Responses

  # ----------------------------------------------------------------------------
  Scenario Outline: Success when Account is exist
    Given a CREATETOKEN request is WELL-FORMED and VALID
    And the current time is <time>
    And the hex-encoding of time is <hex-time>
    And the OperatorAccountID is exist
    And the <sessionkey> is exist

    When the request is processed
	"""
	{
      "PlatformKey": "L100",
      "Sequence": "f82f441f-a20f-4244-b760-35d2d05705d7",
      "Timestamp": "2021-06-07T13:14:15.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID",
      "GameID": "11588"
    }
	"""
    And the player is retrieved
    And the <sessionkey> is retrieved and valid
    And the <sessionkey> is wrapped

    Then a CREATETOKEN response is returned
    And HTTP status is 200
    And the <sessiontoken> length will be <= 50 chars
    And the <sessiontoken> will be of the form <sessionkey>-<hex-time>
    And the response body is:
	"""
	{
      "ErrorCode": 0,
      "Result": {
        "SessionToken": "dd2e8025-3362-400c-b3fe-e54947f8279c-179e5f738d8"
      }
    }
	"""

    Examples:
      | sessionkey                           | time                     | hex-time    | sessiontoken                                     |
      | dd2e8025-3362-400c-b3fe-e54947f8279c | Mon Jun 07 2021 13:14:15 | 179e5f738d8 | dd2e8025-3362-400c-b3fe-e54947f8279c-179e5f738d8 |

  # ----------------------------------------------------------------------------
  Scenario Outline: Failure when Account is exist and SessionKey is expired
    Given a CREATETOKEN request is WELL-FORMED and VALID
    And the current time is <time>
    And the OperatorAccountID is exist
    And the <expired-sessionkey> is expired

    When the request is processed
	"""
	{
      "PlatformKey": "L100",
      "Sequence": "bb16545d-4b4b-4c70-acfa-4f01253a0675",
      "Timestamp": "2021-06-07T13:14:16.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID",
      "GameID": "11588"
    }
	"""
    And the logout date of last login event is <logout-date>

    Then failure response is returned
    And HTTP status code is 200
    And response body is:
    """
    {
      "ErrorCode": -5,
    }
	"""

    Examples:
      | expired-sessionkey                   | time                     | logout-date              |
      | 71d3e749-56c5-4c79-a148-8069c41d201b | Mon Jun 07 2021 13:16:15 | Mon Jun 07 2021 11:16:15 |

  # ----------------------------------------------------------------------------
  Scenario: Failure when Account is not exist
    Given a CREATETOKEN request is WELL-FORMED

    And the OperatorAccountID is not exist

    When the request is processed
    """
    {
      "PlatformKey": "L100",
      "Sequence": "4ca8f42c-9354-4123-a7b5-f91e1723ee2e",
      "Timestamp": "2021-06-07T13:17:15.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID",
      "GameID": "11588"
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
  Scenario: Request is not well formed
    Given a CREATETOKEN request is NOT WELL-FORMED

    When the request is processed

    Then failure response is returned
    And HTTP status is 200
    And a response body is:
    """
    {
      "ErrorCode": -6
    }
    """

  # ----------------------------------------------------------------------------
  Scenario: PlatformKey is not valid
    Given a CREATETOKEN request is WELL-FORMED
    And the specified PLATFORM_KEY is invalid

    When the request is processed
    """
    {
      "PlatformKey": "invalid",
      "Sequence": "86a1b3a9-2573-41a9-a9bd-b2565565228c",
      "Timestamp": "2021-06-07T13:18:15.9546136Z",
      "OperatorAccountID": "livescorebetUK/TestOperatorAccountID",
      "GameID": "11588"
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
  Scenario: Upstream Client Error
    Given a CREATETOKEN request is WELL-FORMED and VALID

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
    Given a CREATETOKEN request is WELL-FORMED and VALID

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
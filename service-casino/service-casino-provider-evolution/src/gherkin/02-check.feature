Feature: CHECK-high-level

  Scenario: On user authentication with Evolution, a /check API call back is made to Lithium from One Wallet to initialize the wallet
    Given a CHECK request is WELL-FORMED and VALID
    And a valid userGuid is send on the CheckUserRequest.userId field
    # Need to validate whether the sid is the session_id that was provided on the user-authentication API
    And a valid sessionId is send on the CheckUserRequest.sid field
    And a valid channelType is send on the CheckUserRequest.channel.type field
    And a unique UUID is send on the CheckUserRequest.uuid field
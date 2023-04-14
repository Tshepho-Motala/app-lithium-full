Feature:Show on player profile the original account
  Scenario:Verify that player link is displayed in original player account
    Given Player_Link
    When Displaying
    Then Return_Primary_Player_Account

  Scenario:Verify that player link is displayed in non-original player account
    Given Player_Link
    When Displaying
    Then Return_Secondary_Player_Account

  Scenario:Verify that original account link is displayed in primary player link column
    Given Primary_Account
    When Displaying
    Then Return_Primary_Player_Link_Column

  Scenario:Verify that non-original account link is displayed in secondary player link column
    Given Secondary_Account
    When Displaying
    Then Return_Secondary_Player_Link_Column

  Scenario:Verify that player link is not displayed in player with LSM account only
    Given Player_Link
    When Displaying
    Then Return_Player_With_LMS_Account_Only



UPDATE `domain_restriction_set`
SET `name` = 'Player Casino Block',
    alt_message_count = 5,
    error_message = null
WHERE `name` = 'Player Casino Opt-Out';
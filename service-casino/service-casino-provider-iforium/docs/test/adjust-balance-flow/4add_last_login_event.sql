USE lithium_user;

DROP FUNCTION IF EXISTS get_player_by_username_and_domain_id;
DROP FUNCTION IF EXISTS get_domain_by_name;

CREATE FUNCTION get_player_by_username_and_domain_id(name text, domain_id bigint) RETURNS bigint DETERMINISTIC
BEGIN
    DECLARE id bigint;
    SELECT u.id INTO id FROM lithium_user.user u WHERE u.username = name AND u.domain_id = domain_id;
    RETURN id;
END;

CREATE FUNCTION get_domain_by_name(name text) RETURNS bigint DETERMINISTIC
BEGIN
    DECLARE id bigint;
    SELECT d.id INTO id FROM lithium_domain.domain d WHERE d.name = name;
    RETURN id;
END;

INSERT INTO lithium_user.login_event (ip_address, user_id, successful, country_code, session_key, domain_id)
VALUES ('127.0.0.1', get_player_by_username_and_domain_id('volodtest1', get_domain_by_name('livescore_uk')), 1, 'GB',
        'ebc83433-c2e0-47f9-b840-4afe7e26e93d',
        get_domain_by_name('livescore_uk')),
       ('127.0.0.1', get_player_by_username_and_domain_id('volodtest1', get_domain_by_name('livescore_ua')), 1, 'GB',
        'ebc83433-c2e0-47f9-b840-4afe7e26e93d',
        get_domain_by_name('livescore_ua'));

DROP FUNCTION IF EXISTS get_player_by_username_and_domain_id;
DROP FUNCTION IF EXISTS get_domain_by_name;
USE lithium_games;

DROP FUNCTION IF EXISTS get_domain_by_name;
DROP FUNCTION IF EXISTS get_game_supplier_by_name_and_domain_id;

CREATE FUNCTION get_domain_by_name(name text) RETURNS bigint DETERMINISTIC
BEGIN
    DECLARE id bigint;
    SELECT d.id INTO id FROM lithium_games.domain d WHERE d.name = name;
    RETURN id;
END;

CREATE FUNCTION get_game_supplier_by_name_and_domain_id(name text, domain_id bigint) RETURNS bigint DETERMINISTIC
BEGIN
    DECLARE id bigint;
    SELECT s.id INTO id FROM lithium_games.game_supplier s WHERE s.name = name and s.domain_id = domain_id;
    RETURN id;
END;

INSERT INTO lithium_games.domain (name, version)
VALUES ('livescore_uk', 0),
       ('livescore_ua', 0);

INSERT INTO lithium_games.game_supplier (deleted, name, version, domain_id)
VALUES (false, 'test', 0, get_domain_by_name('livescore_uk')),
       (false, 'test', 0, get_domain_by_name('livescore_ua'));

INSERT INTO lithium_games.game (enabled, guid, version, visible, domain_id, game_supplier_id)
VALUES (true, 'service-casino-provider-iforium_11588', 0, true, get_domain_by_name('livescore_uk'),
        get_game_supplier_by_name_and_domain_id('test', get_domain_by_name('livescore_uk'))),
       (true, 'service-casino-provider-iforium_11588', 0, true, get_domain_by_name('livescore_ua'),
        get_game_supplier_by_name_and_domain_id('test', get_domain_by_name('livescore_ua')));

DROP FUNCTION IF EXISTS get_domain_by_name;
DROP FUNCTION IF EXISTS get_game_supplier_by_name_and_domain_id;
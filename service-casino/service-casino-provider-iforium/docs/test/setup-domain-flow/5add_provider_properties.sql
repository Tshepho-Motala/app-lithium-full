USE lithium_domain;

DROP FUNCTION IF EXISTS get_provider_by_name_and_domain_id;
DROP FUNCTION IF EXISTS get_domain_by_name;

CREATE FUNCTION get_provider_by_name_and_domain_id(provider_name text, domain_id bigint) RETURNS bigint
    DETERMINISTIC
BEGIN
    DECLARE
        id bigint;
    SELECT p.id
    INTO id
    FROM lithium_domain.provider p
    WHERE p.name = provider_name
      and p.domain_id = domain_id;
    RETURN id;
END;

CREATE FUNCTION get_domain_by_name(name text) RETURNS bigint
    DETERMINISTIC
BEGIN
    DECLARE
        id bigint;
    SELECT d.id
    INTO id
    FROM lithium_domain.domain d
    WHERE d.name = name;
    RETURN id;
END;

CREATE PROCEDURE set_provider_properties(IN provider_id bigint(20))
BEGIN
    INSERT INTO lithium_domain.provider_property (name, value, version, provider_id)
    VALUES ('whitelistIPs', '127.0.0.1', 1, provider_id),
           ('secureUserPassword', '/TtP9P9bH71qWHr35p39SQ==', 1, provider_id),
           ('secureUserName', '/TtP9P9bH71qWHr35p39SQ==', 1, provider_id),
           ('lobbyurl', 'https://www.livescorebet.com/uk', 1, provider_id),
           ('startGameUrl', 'https://gameflex-s000.iforium.com/gamelaunch/api/v2.0/game-launchers/gul/v1/launch', 1, provider_id),
           ('casinoid', 'S009-LVS-142', 1, provider_id),
           ('listgame', 'https://drive.google.com/u/0/uc?id=1NWNDI4UKdLRpAzzZfpcmlBGhvdY-f3n-&export=download', 1, provider_id),
           ('regulationsEnabled', 'true', 1, provider_id),
           ('regulationSessionDuration', '0', 1, provider_id),
           ('regulationInterval', '86400', 1, provider_id),
           ('regulationGameHistoryUrl', 'https://www.livescorebet.com/uk/history', 1, provider_id),
           ('regulationBonusUrl', 'https://www.livescorebet.com/uk/promotions', 1, provider_id),
           ('regulationOverrideRts13Mode', 'disabled', 1, provider_id),
           ('regulationOverrideCmaMode', 'disabled', 1, provider_id),
           ('blueprintProgressiveJackpotFeedUrl', 'https://sapirgsuat.blueprintgaming.com/iforium/SAPI.asmx/Progressive?currency=GBP&sid=142', 1, provider_id)
END;

SELECT @iforium_uk_provider:= get_provider_by_name_and_domain_id('Iforium', get_domain_by_name('livescore_uk'));
SELECT @iforium_ua_provider:= get_provider_by_name_and_domain_id('Iforium', get_domain_by_name('livescore_ua'));

CALL set_provider_properties(@iforium_uk_provider);
CALL set_provider_properties(@iforium_ua_provider);

DROP FUNCTION IF EXISTS get_provider_by_name_and_domain_id;
DROP FUNCTION IF EXISTS get_domain_by_name;
DROP PROCEDURE IF EXISTS set_provider_properties;

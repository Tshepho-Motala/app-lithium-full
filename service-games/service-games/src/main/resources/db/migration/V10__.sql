-- Remove slotapi games from existing recently played games
DELETE FROM `recently_played`
WHERE `game_id` IN (SELECT `id` FROM `game` WHERE `provider_guid` = 'service-casino-provider-slotapi');

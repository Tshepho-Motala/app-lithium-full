SET FOREIGN_KEY_CHECKS = 0;

ALTER TABLE `game_guid` RENAME TO `game`;
ALTER TABLE `game` DROP INDEX `idx_game_guid`;
ALTER TABLE `game`
    CHANGE COLUMN `game_guid` `guid` varchar(255) NOT NULL,
    ADD COLUMN `version` int(11) NOT NULL;
CREATE UNIQUE INDEX `idx_guid` ON `game`(`guid`) ALGORITHM INPLACE LOCK NONE;

ALTER TABLE `bonus_round_track` DROP FOREIGN KEY `FKc3j1hamp4ptfjunxdlvvdppp2`;
ALTER TABLE `bonus_round_track` DROP FOREIGN KEY `FK8xasd6p7id2ug802j422ev3uf`;
ALTER TABLE `bonus_round_track` DROP INDEX `idx_brt_player_game_round`;
ALTER TABLE `bonus_round_track` CHANGE COLUMN `game_guid_id` `game_id` bigint(20) NOT NULL;
ALTER TABLE `bonus_round_track` ADD FOREIGN KEY `fk_game` (`game_id`) REFERENCES `game` (`id`);
ALTER TABLE `bonus_round_track` ADD FOREIGN KEY `FK8xasd6p7id2ug802j422ev3uf` (`player_bonus_id`) REFERENCES `player_bonus` (`id`);
CREATE UNIQUE INDEX `idx_brt_player_game_round` ON `bonus_round_track` (`player_bonus_id`,`game_id`,`round_id`) ALGORITHM INPLACE LOCK NONE;

SET FOREIGN_KEY_CHECKS = 1;
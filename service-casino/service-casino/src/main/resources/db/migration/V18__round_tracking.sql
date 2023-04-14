CREATE TABLE `game_guid` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `game_guid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_game_guid` (`game_guid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_round_track` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `completed` bit(1) NOT NULL,
  `completed_date` datetime DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `round_id` varchar(255) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `game_guid_id` bigint(20) NOT NULL,
  `player_bonus_id` bigint(20) NOT NULL,
  `player_bonus_history_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_brt_player_game_round` (`player_bonus_id`,`game_guid_id`,`round_id`),
  KEY `idx_brt_player_bonus_completed` (`player_bonus_history_id`,`completed`),
  KEY `FKc3j1hamp4ptfjunxdlvvdppp2` (`game_guid_id`),
  CONSTRAINT `FK8xasd6p7id2ug802j422ev3uf` FOREIGN KEY (`player_bonus_id`) REFERENCES `player_bonus` (`id`),
  CONSTRAINT `FKc3j1hamp4ptfjunxdlvvdppp2` FOREIGN KEY (`game_guid_id`) REFERENCES `game_guid` (`id`),
  CONSTRAINT `FKo7ycbrdc1ak3jjimx3tdkn9rr` FOREIGN KEY (`player_bonus_history_id`) REFERENCES `player_bonus_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


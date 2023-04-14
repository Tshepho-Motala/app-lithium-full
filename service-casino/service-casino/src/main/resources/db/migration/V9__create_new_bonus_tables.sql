CREATE TABLE `player_bonus_freespin_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `freespins_remaining` int(11) DEFAULT NULL,
  `ext_bonus_id` int(11) DEFAULT NULL,
  `bonus_rules_freespins_id` bigint(20) NOT NULL,
  `player_bonus_history_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKt58jqxp7tt8l8867witx4y1l1` (`bonus_rules_freespins_id`),
  KEY `FKex3v1k1ax6l7vhvxaoi6bfjuj` (`player_bonus_history_id`),
  CONSTRAINT `FKex3v1k1ax6l7vhvxaoi6bfjuj` FOREIGN KEY (`player_bonus_history_id`) REFERENCES `player_bonus_history` (`id`),
  CONSTRAINT `FKt58jqxp7tt8l8867witx4y1l1` FOREIGN KEY (`bonus_rules_freespins_id`) REFERENCES `bonus_rules_freespins` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `bonus_rules_freespin_games` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `game_id` varchar(255) DEFAULT NULL,
  `bonus_rules_freespins_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqkygwa6x5gc27q2mux97tuqaf` (`bonus_rules_freespins_id`),
  CONSTRAINT `FKqkygwa6x5gc27q2mux97tuqaf` FOREIGN KEY (`bonus_rules_freespins_id`) REFERENCES `bonus_rules_freespins` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `bonus_rules_freespins` ADD COLUMN `provider` varchar(100) DEFAULT NULL;
ALTER TABLE `bonus_rules_freespins` ADD COLUMN `wager_requirements` int(11) DEFAULT NULL;
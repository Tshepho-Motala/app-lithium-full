CREATE TABLE `bonus_external_game_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `campaign_id` bigint(20) DEFAULT NULL,
  `provider` varchar(255) DEFAULT NULL,
  `bonus_revision_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK27rlw2v0qsohstacwhybo2fp3` (`bonus_revision_id`),
  CONSTRAINT `FK27rlw2v0qsohstacwhybo2fp3` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_revision_bonus_external_game_configs` (
  `bonus_revision_id` bigint(20) NOT NULL,
  `bonus_external_game_configs_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_a25op0ynpt2n94y4g86yt0ue4` (`bonus_external_game_configs_id`),
  KEY `FKabtdlxuc2wfyvfcfr0sxdk6fm` (`bonus_revision_id`),
  CONSTRAINT `FKabtdlxuc2wfyvfcfr0sxdk6fm` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`),
  CONSTRAINT `FKnsxdxmrm850m4rgbabapi796r` FOREIGN KEY (`bonus_external_game_configs_id`) REFERENCES `bonus_external_game_config` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_bonus_external_game_link` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `external_game_url` varchar(255) DEFAULT NULL,
  `bonus_external_game_config_id` bigint(20) NOT NULL,
  `player_bonus_history_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjx9dor0cb3s64mkq4plilrvlr` (`bonus_external_game_config_id`),
  KEY `FKo61ofl1x2ryxk8ay9f6yd34r` (`player_bonus_history_id`),
  CONSTRAINT `FKjx9dor0cb3s64mkq4plilrvlr` FOREIGN KEY (`bonus_external_game_config_id`) REFERENCES `bonus_external_game_config` (`id`),
  CONSTRAINT `FKo61ofl1x2ryxk8ay9f6yd34r` FOREIGN KEY (`player_bonus_history_id`) REFERENCES `player_bonus_history` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

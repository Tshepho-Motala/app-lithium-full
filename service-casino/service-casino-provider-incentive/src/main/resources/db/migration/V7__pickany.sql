/*!40101 SET NAMES utf8 */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40101 SET character_set_client = utf8 */;
/*!40014 SET FOREIGN_KEY_CHECKS=0 */;

CREATE TABLE `pick_any_entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` bigint(20) NOT NULL,
  `entry_timestamp` datetime DEFAULT NULL,
  `entry_transaction_id` varchar(255) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `predictor_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `competition_id` bigint(20) NOT NULL,
  `domain_id` bigint(20) NOT NULL,
  `game_id` bigint(20) NOT NULL,
  `settlement_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`entry_transaction_id`),
  KEY `FKdtn37bb82pal3uvnaaqb2por4` (`competition_id`),
  KEY `FKn3yrcks6hcqatrrm4tyeisdrf` (`domain_id`),
  KEY `FKoxgm5i881yyxje998q9wqn0qv` (`game_id`),
  KEY `FKqkd32bb1kgjbl70lre1v9g4i7` (`settlement_id`),
  KEY `FKrra8gilkqk539df615udyej3y` (`user_id`),
  CONSTRAINT `FKdtn37bb82pal3uvnaaqb2por4` FOREIGN KEY (`competition_id`) REFERENCES `competition` (`id`),
  CONSTRAINT `FKn3yrcks6hcqatrrm4tyeisdrf` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`),
  CONSTRAINT `FKoxgm5i881yyxje998q9wqn0qv` FOREIGN KEY (`game_id`) REFERENCES `pick_any_game` (`id`),
  CONSTRAINT `FKqkd32bb1kgjbl70lre1v9g4i7` FOREIGN KEY (`settlement_id`) REFERENCES `pick_any_settlement` (`id`),
  CONSTRAINT `FKrra8gilkqk539df615udyej3y` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pick_any_entry_pick` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `away_score` bigint(20) NOT NULL,
  `home_score` bigint(20) NOT NULL,
  `incentive_event_id` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `entry_id` bigint(20) NOT NULL,
  `event_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK45n067k6v2cj3vjnm6uajmp1j` (`entry_id`),
  KEY `FK142tvyw4yfeuiew3392puf0do` (`event_id`),
  CONSTRAINT `FK142tvyw4yfeuiew3392puf0do` FOREIGN KEY (`event_id`) REFERENCES `event` (`id`),
  CONSTRAINT `FK45n067k6v2cj3vjnm6uajmp1j` FOREIGN KEY (`entry_id`) REFERENCES `pick_any_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pick_any_game` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `code` varchar(255) NOT NULL,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pick_any_settlement` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` bigint(20) NOT NULL,
  `modified_date` bigint(20) NOT NULL,
  `settlement_timestamp` datetime DEFAULT NULL,
  `settlement_transaction_id` varchar(255) NOT NULL,
  `total_points_result` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `entry_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_tran_id` (`settlement_transaction_id`),
  KEY `FKktorti1xycjleifamqur1pcrw` (`entry_id`),
  CONSTRAINT `FKktorti1xycjleifamqur1pcrw` FOREIGN KEY (`entry_id`) REFERENCES `pick_any_entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `pick_any_settlement_pick` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `away_score_result` bigint(20) NOT NULL,
  `home_score_result` bigint(20) NOT NULL,
  `points_result` bigint(20) NOT NULL,
  `version` int(11) NOT NULL,
  `entry_pick_id` bigint(20) NOT NULL,
  `settlement_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK8tywvkqppmck2mdtuy7kwbpl0` (`entry_pick_id`),
  KEY `FKcop844ws5sdphoofiy5m2yh2u` (`settlement_id`),
  CONSTRAINT `FK8tywvkqppmck2mdtuy7kwbpl0` FOREIGN KEY (`entry_pick_id`) REFERENCES `pick_any_entry_pick` (`id`),
  CONSTRAINT `FKcop844ws5sdphoofiy5m2yh2u` FOREIGN KEY (`settlement_id`) REFERENCES `pick_any_settlement` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=1 */;


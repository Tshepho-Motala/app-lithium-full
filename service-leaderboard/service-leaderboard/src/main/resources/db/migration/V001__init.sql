CREATE TABLE `domain` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_domain_name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;


CREATE TABLE `leaderboard` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` int(11) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `duration_granularity` int(11) NOT NULL,
  `duration_period` int(11) DEFAULT NULL,
  `enabled` bit(1) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `notification` varchar(255) DEFAULT NULL,
  `notification_non_top` varchar(255) DEFAULT NULL,
  `recurrence_pattern` varchar(255) DEFAULT NULL,
  `score_to_points` decimal(19,2) DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `visible` bit(1) DEFAULT NULL,
  `xp_level_max` int(11) DEFAULT NULL,
  `xp_level_min` int(11) DEFAULT NULL,
  `xp_points_granularity` int(11) NOT NULL,
  `xp_points_max` decimal(19,2) DEFAULT NULL,
  `xp_points_min` decimal(19,2) DEFAULT NULL,
  `xp_points_period` int(11) DEFAULT NULL,
  `domain_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_leaderboard` (`domain_id`,`xp_level_min`,`xp_level_max`,`xp_points_min`,`xp_points_max`,`xp_points_period`,`xp_points_granularity`,`start_date`,`recurrence_pattern`),
  CONSTRAINT `FK9ome4oti3kgoakxa8gvn7bibp` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `leaderboard_conversion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `conversion` decimal(19,2) DEFAULT NULL,
  `type` int(11) NOT NULL,
  `version` int(11) NOT NULL,
  `leaderboard_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`leaderboard_id`,`type`),
  CONSTRAINT `FK8n8b7a2l0bgv9yl087c0yg3mb` FOREIGN KEY (`leaderboard_id`) REFERENCES `leaderboard` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `leaderboard_history` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `closed` bit(1) DEFAULT NULL,
  `end_date` datetime NOT NULL,
  `start_date` datetime NOT NULL,
  `version` int(11) NOT NULL,
  `leaderboard_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`leaderboard_id`,`start_date`,`end_date`),
  CONSTRAINT `FKlxtheh295kqk3ex9px7jw7y10` FOREIGN KEY (`leaderboard_id`) REFERENCES `leaderboard` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `leaderboard_place_notification` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bonus_code` varchar(255) DEFAULT NULL,
  `notification` varchar(255) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `leaderboard_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`leaderboard_id`,`rank`),
  CONSTRAINT `FKtf313jud4garg2cxpk3n0lqla` FOREIGN KEY (`leaderboard_id`) REFERENCES `leaderboard` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `guid` varchar(255) NOT NULL,
  `opt_out` bit(1) DEFAULT NULL,
  `version` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_guid` (`guid`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE `entry` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `points` decimal(19,2) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `score` decimal(19,2) DEFAULT NULL,
  `version` int(11) NOT NULL,
  `leaderboard_history_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_name` (`leaderboard_history_id`,`user_id`),
  KEY `FKb8w0fw4ccf95p9ct3y2gn4nbq` (`user_id`),
  CONSTRAINT `FKb8w0fw4ccf95p9ct3y2gn4nbq` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `FKc1u84qo0nxqw1hij3nk8q7aja` FOREIGN KEY (`leaderboard_history_id`) REFERENCES `leaderboard_history` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
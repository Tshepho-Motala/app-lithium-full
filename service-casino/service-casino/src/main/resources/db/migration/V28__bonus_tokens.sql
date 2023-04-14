CREATE TABLE `bonus_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `amount` bigint(20) NOT NULL,
  `currency` varchar(255) NOT NULL,
  `minimum_odds` double DEFAULT NULL,
  `version` int(11) NOT NULL,
  `bonus_revision_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKr4k4k49wfjw1a6dj80vd1l7e` (`bonus_revision_id`),
  CONSTRAINT `FKr4k4k49wfjw1a6dj80vd1l7e` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `bonus_revision_bonus_tokens` (
  `bonus_revision_id` bigint(20) NOT NULL,
  `bonus_tokens_id` bigint(20) NOT NULL,
  UNIQUE KEY `UK_758bvp4tmxs38t84rsc5u1q83` (`bonus_tokens_id`),
  KEY `FK945d5aukegr9csf2cwknu8wmu` (`bonus_revision_id`),
  CONSTRAINT `FK13esxq1moo0aqvuq44cjc78lq` FOREIGN KEY (`bonus_tokens_id`) REFERENCES `bonus_token` (`id`),
  CONSTRAINT `FK945d5aukegr9csf2cwknu8wmu` FOREIGN KEY (`bonus_revision_id`) REFERENCES `bonus_revision` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `player_bonus_token` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `created_date` datetime NOT NULL,
  `custom_token_amount_cents` bigint(20) DEFAULT NULL,
  `expiry_date` datetime NOT NULL,
  `status` int(11) NOT NULL,
  `bonus_token_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `player_bonus_history_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_pbt_player_guid` (`user_id`,`status`),
  KEY `idx_pbt_status_expiry_date` (`status`,`expiry_date`),
  KEY `FK2xp6e0i0ppp2wemk5ke44ks1e` (`bonus_token_id`),
  CONSTRAINT `FK2xp6e0i0ppp2wemk5ke44ks1e` FOREIGN KEY (`bonus_token_id`) REFERENCES `bonus_token` (`id`),
  CONSTRAINT `FK6gmvpp4t6cq6bh0mc84jxno4j` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE `player_bonus_history`
    ADD COLUMN `custom_bonus_token_amount_cents` bigint(20) DEFAULT NULL;

